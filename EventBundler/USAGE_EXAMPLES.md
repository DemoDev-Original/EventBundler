# EventBundler Usage Examples

Practical examples and use cases for EventBundler's 110 customizable events.

---

## Table of Contents
1. [Economy System Integration](#economy-system-integration)
2. [Custom Leveling System](#custom-leveling-system)
3. [Combat Tag System](#combat-tag-system)
4. [Shop System with Discounts](#shop-system-with-discounts)
5. [Party Quest System](#party-quest-system)
6. [Anti-Cheat Integration](#anti-cheat-integration)
7. [Minigame Framework](#minigame-framework)
8. [Custom Admin Tools](#custom-admin-tools)
9. [Achievement System](#achievement-system)
10. [Advanced Automation](#advanced-automation)

---

## 1. Economy System Integration

### Scenario: Track all money transfers and apply taxes

```java
public class EconomyIntegration implements Listener {
    
    private final double TAX_RATE = 0.05; // 5% tax
    private final double TAX_THRESHOLD = 1000.0;
    
    @EventHandler
    public void onMoneyTransfer(EBMoneyTransferEvent event) {
        double amount = event.getAmount();
        
        // Apply tax on large transfers
        if (amount >= TAX_THRESHOLD) {
            double tax = amount * TAX_RATE;
            event.setAmount(amount - tax);
            
            event.getSender().sendMessage(ChatColor.YELLOW + 
                "Tax applied: $" + String.format("%.2f", tax));
            
            // Log to database
            logTaxCollection(event.getSender(), tax);
        }
        
        // Block suspicious transfers
        if (amount > 100000) {
            event.setCancelled(true);
            event.getSender().sendMessage(ChatColor.RED + 
                "Transfer amount too large! Contact an admin.");
            alertStaff(event);
        }
    }
    
    @EventHandler
    public void onTransferCancel(EBTransferCancelEvent event) {
        // Refund or notify
        if (event.getReason().equals("insufficient_funds")) {
            event.getSender().sendMessage(ChatColor.RED + 
                "Transfer failed: Not enough money!");
        }
    }
    
    private void logTaxCollection(Player player, double tax) {
        // Database logging
    }
    
    private void alertStaff(EBMoneyTransferEvent event) {
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.hasPermission("economy.alerts"))
            .forEach(staff -> staff.sendMessage(
                ChatColor.RED + "[ALERT] Large transfer attempt: " + 
                event.getSender().getName() + " -> " + 
                event.getReceiver().getName() + 
                " ($" + event.getAmount() + ")"
            ));
    }
}
```

---

## 2. Custom Leveling System

### Scenario: Complete RPG leveling with rewards and progression

```java
public class LevelingSystem implements Listener {
    
    private final Map<UUID, PlayerLevel> playerLevels = new HashMap<>();
    private final EventBundlerAPI api = EventBundler.getApi();
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        loadPlayerLevel(event.getPlayer());
    }
    
    public void addExperience(Player player, double xp) {
        PlayerLevel level = playerLevels.get(player.getUniqueId());
        level.addExperience(xp);
        
        if (level.shouldLevelUp()) {
            int newLevel = level.getLevel() + 1;
            
            EBPlayerLevelUpEvent levelEvent = new EBPlayerLevelUpEvent(
                player,
                level.getLevel(),
                newLevel,
                "RPG",
                calculateReward(newLevel)
            );
            
            api.fireEvent(levelEvent);
            
            if (!levelEvent.isCancelled()) {
                level.setLevel(levelEvent.getNewLevel());
                giveRewards(player, levelEvent.getReward());
                
                // Broadcast milestone levels
                if (newLevel % 10 == 0) {
                    Bukkit.broadcastMessage(ChatColor.GOLD + 
                        player.getName() + " reached level " + newLevel + "!");
                }
            }
        }
    }
    
    @EventHandler
    public void onLevelUp(EBPlayerLevelUpEvent event) {
        Player player = event.getPlayer();
        int newLevel = event.getNewLevel();
        
        // Play effects
        player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, null);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        
        // Unlock abilities at specific levels
        if (newLevel == 10) {
            unlockAbility(player, "double_jump");
        } else if (newLevel == 25) {
            unlockAbility(player, "dash");
        } else if (newLevel == 50) {
            unlockAbility(player, "fly");
            event.setReward(event.getReward() * 2); // Bonus reward
        }
        
        // Title notification
        player.sendTitle(
            ChatColor.GOLD + "LEVEL UP!",
            ChatColor.YELLOW + "Level " + newLevel,
            10, 70, 20
        );
    }
    
    private double calculateReward(int level) {
        return level * 100 + (level * level * 10);
    }
    
    private void giveRewards(Player player, double amount) {
        // Give money, items, etc.
    }
    
    private void unlockAbility(Player player, String ability) {
        player.sendMessage(ChatColor.GREEN + "New ability unlocked: " + ability);
    }
    
    private void loadPlayerLevel(Player player) {
        // Load from database
    }
}
```

---

## 3. Combat Tag System

### Scenario: Tag players in combat and prevent exploits

```java
public class CombatTagSystem implements Listener {
    
    private final Map<UUID, CombatTag> activeCombat = new HashMap<>();
    private final int COMBAT_DURATION = 15; // seconds
    private final EventBundlerAPI api = EventBundler.getApi();
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player victim = (Player) event.getEntity();
        Entity damagerEntity = event.getDamager();
        
        Player attacker = null;
        if (damagerEntity instanceof Player) {
            attacker = (Player) damagerEntity;
        } else if (damagerEntity instanceof Projectile) {
            Projectile proj = (Projectile) damagerEntity;
            if (proj.getShooter() instanceof Player) {
                attacker = (Player) proj.getShooter();
            }
        }
        
        if (attacker == null) return;
        
        // Fire combat start event
        EBCombatStartEvent combatEvent = new EBCombatStartEvent(
            victim,
            attacker,
            EBCombatStartEvent.CombatType.PVP,
            COMBAT_DURATION
        );
        
        api.fireEvent(combatEvent);
        
        if (combatEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        
        // Tag both players
        tagPlayer(victim, attacker, combatEvent.getCombatDuration());
        tagPlayer(attacker, victim, combatEvent.getCombatDuration());
    }
    
    private void tagPlayer(Player player, Player opponent, int duration) {
        CombatTag tag = new CombatTag(player, opponent, duration);
        activeCombat.put(player.getUniqueId(), tag);
        
        player.sendMessage(ChatColor.RED + "⚔ Combat Mode Activated! " + 
            "Do not log out for " + duration + " seconds!");
        
        // Start countdown task
        new BukkitRunnable() {
            int remaining = duration;
            
            @Override
            public void run() {
                if (remaining <= 0 || !player.isOnline()) {
                    endCombat(player);
                    cancel();
                    return;
                }
                
                if (remaining <= 5) {
                    player.sendMessage(ChatColor.YELLOW + 
                        "Combat ends in " + remaining + " seconds");
                }
                
                remaining--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    
    private void endCombat(Player player) {
        activeCombat.remove(player.getUniqueId());
        
        EBCombatEndEvent endEvent = new EBCombatEndEvent(
            player,
            CombatEndReason.TIMEOUT
        );
        api.fireEvent(endEvent);
        
        player.sendMessage(ChatColor.GREEN + "✓ You are no longer in combat");
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        if (isInCombat(player)) {
            // Combat log penalty
            player.setHealth(0); // Kill player
            
            Bukkit.broadcastMessage(ChatColor.RED + 
                player.getName() + " logged out during combat!");
            
            // Drop items
            ItemStack[] contents = player.getInventory().getContents();
            Location loc = player.getLocation();
            for (ItemStack item : contents) {
                if (item != null) {
                    loc.getWorld().dropItemNaturally(loc, item);
                }
            }
        }
    }
    
    public boolean isInCombat(Player player) {
        return activeCombat.containsKey(player.getUniqueId());
    }
}
```

---

## 4. Shop System with Discounts

### Scenario: Dynamic shop with VIP discounts and purchase limits

```java
public class DiscountShopSystem implements Listener {
    
    private final Map<UUID, Integer> dailyPurchases = new HashMap<>();
    private final int DAILY_LIMIT = 10;
    
    @EventHandler
    public void onShopPurchase(EBShopPurchaseEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        // Check daily limit
        int purchases = dailyPurchases.getOrDefault(playerId, 0);
        if (purchases >= DAILY_LIMIT) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + 
                "Daily purchase limit reached! (" + DAILY_LIMIT + ")");
            return;
        }
        
        // Apply VIP discounts
        if (player.hasPermission("shop.vip")) {
            event.setDiscount(0.10); // 10% discount
        } else if (player.hasPermission("shop.mvp")) {
            event.setDiscount(0.20); // 20% discount
        }
        
        // Apply bulk purchase discount
        if (event.getQuantity() >= 64) {
            event.setDiscount(event.getDiscount() + 0.05); // +5% bulk discount
        }
        
        // First purchase bonus
        if (purchases == 0) {
            event.setDiscount(event.getDiscount() + 0.15); // +15% first buy
            player.sendMessage(ChatColor.GREEN + 
                "First purchase bonus: +15% discount!");
        }
        
        // Calculate and display final price
        double finalPrice = event.getFinalPrice();
        player.sendMessage(ChatColor.GOLD + "Final price: $" + 
            String.format("%.2f", finalPrice));
        
        if (event.getDiscount() > 0) {
            player.sendMessage(ChatColor.GREEN + "Discount: " + 
                (event.getDiscount() * 100) + "%");
        }
        
        // Track purchase
        dailyPurchases.put(playerId, purchases + 1);
    }
    
    @EventHandler
    public void onShopSell(EBShopSellEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        // Bonus for bulk selling
        if (event.getQuantity() >= 64) {
            double bonus = event.getPrice() * 0.10;
            event.setPrice(event.getPrice() + bonus);
            player.sendMessage(ChatColor.GREEN + 
                "Bulk sell bonus: +$" + String.format("%.2f", bonus));
        }
    }
}
```

---

## 5. Party Quest System

### Scenario: Cooperative quests with party mechanics

```java
public class PartyQuestSystem implements Listener {
    
    private final Map<String, Party> activeParties = new HashMap<>();
    private final EventBundlerAPI api = EventBundler.getApi();
    
    @EventHandler
    public void onPartyCreate(EBPartyCreateEvent event) {
        Player leader = event.getLeader();
        String partyId = event.getPartyId();
        
        Party party = new Party(partyId, leader);
        activeParties.put(partyId, party);
        
        leader.sendMessage(ChatColor.GREEN + 
            "Party created! Use /party invite <player> to invite members.");
    }
    
    @EventHandler
    public void onPartyJoin(EBPartyJoinEvent event) {
        Player player = event.getPlayer();
        String partyId = event.getPartyId();
        
        Party party = activeParties.get(partyId);
        if (party != null) {
            party.addMember(player);
            
            // Notify all members
            party.broadcast(ChatColor.YELLOW + 
                player.getName() + " joined the party!");
            
            // Check if party is ready for quest
            if (party.getSize() >= 3) {
                party.broadcast(ChatColor.GREEN + 
                    "Party ready! Use /quest start to begin.");
            }
        }
    }
    
    public void startQuest(Party party, String questId) {
        EBQuestStartEvent questEvent = new EBQuestStartEvent(
            party.getLeader(),
            questId,
            party.getMemberIds()
        );
        
        api.fireEvent(questEvent);
        
        if (questEvent.isCancelled()) {
            party.broadcast(ChatColor.RED + "Quest could not be started!");
            return;
        }
        
        // Initialize quest
        party.setActiveQuest(questId);
        party.broadcast(ChatColor.GOLD + "Quest '" + questId + "' started!");
        
        // Track party progress
        trackPartyProgress(party, questId);
    }
    
    @EventHandler
    public void onQuestComplete(EBQuestCompleteEvent event) {
        Player player = event.getPlayer();
        String questId = event.getQuestId();
        
        Party party = findPlayerParty(player);
        if (party != null && party.getActiveQuest().equals(questId)) {
            // Reward all party members
            double rewardPerMember = event.getReward() / party.getSize();
            
            party.getMembers().forEach(member -> {
                giveReward(member, rewardPerMember);
                member.sendMessage(ChatColor.GREEN + 
                    "Quest completed! Reward: $" + 
                    String.format("%.2f", rewardPerMember));
            });
            
            // Party bonus
            if (party.getSize() == 4) {
                party.broadcast(ChatColor.GOLD + 
                    "Full party bonus: +25% rewards!");
            }
        }
    }
    
    private Party findPlayerParty(Player player) {
        return activeParties.values().stream()
            .filter(p -> p.isMember(player))
            .findFirst()
            .orElse(null);
    }
    
    private void trackPartyProgress(Party party, String questId) {
        // Implement quest tracking
    }
    
    private void giveReward(Player player, double amount) {
        // Give rewards
    }
}
```

---

## 6. Anti-Cheat Integration

### Scenario: Detect suspicious behavior using events

```java
public class AntiCheatSystem implements Listener {
    
    private final Map<UUID, PlayerStats> playerStats = new HashMap<>();
    private final EventBundlerAPI api = EventBundler.getApi();
    
    @EventHandler
    public void onComboHit(EBComboHitEvent event) {
        Player player = event.getPlayer();
        int comboCount = event.getComboCount();
        
        PlayerStats stats = playerStats.computeIfAbsent(
            player.getUniqueId(), 
            k -> new PlayerStats()
        );
        
        stats.recordCombo(comboCount);
        
        // Detect impossible combos
        if (comboCount > 20 && stats.getAverageCPS() > 15) {
            flagPlayer(player, "Impossible combo: " + comboCount + 
                " hits, " + stats.getAverageCPS() + " CPS");
        }
    }
    
    @EventHandler
    public void onCriticalHit(EBCriticalHitEvent event) {
        Player player = event.getPlayer();
        
        PlayerStats stats = playerStats.get(player.getUniqueId());
        if (stats != null) {
            stats.recordCritical();
            
            // Check crit rate
            double critRate = stats.getCriticalHitRate();
            if (critRate > 0.80) { // 80% crit rate is suspicious
                flagPlayer(player, "Abnormal critical hit rate: " + 
                    (critRate * 100) + "%");
            }
        }
    }
    
    @EventHandler
    public void onPlayerAFK(EBPlayerAFKEvent event) {
        // Clear stats when AFK to avoid false positives
        playerStats.remove(event.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onMoneyTransfer(EBMoneyTransferEvent event) {
        // Detect money laundering patterns
        if (event.getAmount() > 50000) {
            logSuspiciousTransaction(event);
            
            // Check if accounts are related
            if (isSameIPAddress(event.getSender(), event.getReceiver())) {
                event.setCancelled(true);
                alertStaff("Possible alt account money transfer: " + 
                    event.getSender().getName() + " -> " + 
                    event.getReceiver().getName());
            }
        }
    }
    
    private void flagPlayer(Player player, String reason) {
        // Send alert to staff
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.hasPermission("anticheat.alerts"))
            .forEach(staff -> staff.sendMessage(
                ChatColor.RED + "[AC] " + player.getName() + ": " + reason
            ));
        
        // Log to file/database
        logViolation(player, reason);
    }
    
    private boolean isSameIPAddress(Player p1, Player p2) {
        return p1.getAddress().getAddress().equals(p2.getAddress().getAddress());
    }
    
    private void alertStaff(String message) {
        // Send alert
    }
    
    private void logViolation(Player player, String reason) {
        // Log violation
    }
    
    private void logSuspiciousTransaction(EBMoneyTransferEvent event) {
        // Log transaction
    }
}
```

---

## 7. Minigame Framework

### Scenario: Generic minigame system using EventBundler

```java
public class MinigameFramework implements Listener {
    
    private final Map<String, Minigame> activeGames = new HashMap<>();
    private final EventBundlerAPI api = EventBundler.getApi();
    
    @EventHandler
    public void onMinigameJoin(EBMinigameJoinEvent event) {
        Player player = event.getPlayer();
        String gameId = event.getGameId();
        
        Minigame game = activeGames.get(gameId);
        if (game == null) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Game not found!");
            return;
        }
        
        if (game.isFull()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Game is full!");
            return;
        }
        
        if (game.hasStarted()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Game already started!");
            return;
        }
        
        game.addPlayer(player);
        game.broadcast(ChatColor.GREEN + player.getName() + " joined! (" + 
            game.getPlayerCount() + "/" + game.getMaxPlayers() + ")");
        
        // Auto-start when full
        if (game.isFull()) {
            startMinigame(game);
        }
    }
    
    @EventHandler
    public void onMinigameLeave(EBMinigameLeaveEvent event) {
        Player player = event.getPlayer();
        String gameId = event.getGameId();
        
        Minigame game = activeGames.get(gameId);
        if (game != null) {
            game.removePlayer(player);
            
            // End game if too few players
            if (game.hasStarted() && game.getPlayerCount() < game.getMinPlayers()) {
                endMinigame(game, "Not enough players");
            }
        }
    }
    
    private void startMinigame(Minigame game) {
        EBMinigameStartEvent startEvent = new EBMinigameStartEvent(
            game.getId(),
            game.getPlayers(),
            game.getSettings()
        );
        
        api.fireEvent(startEvent);
        
        if (startEvent.isCancelled()) {
            game.broadcast(ChatColor.RED + "Failed to start game!");
            return;
        }
        
        game.start();
        game.broadcast(ChatColor.GOLD + "=== GAME STARTING ===");
        game.broadcast(ChatColor.YELLOW + "Type: " + game.getType());
        game.broadcast(ChatColor.YELLOW + "Duration: " + game.getDuration() + " seconds");
        
        // Countdown
        new BukkitRunnable() {
            int countdown = 5;
            
            @Override
            public void run() {
                if (countdown == 0) {
                    game.broadcast(ChatColor.GREEN + "GO!");
                    game.startGameplay();
                    cancel();
                    return;
                }
                
                game.broadcast(ChatColor.YELLOW + "" + countdown + "...");
                game.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                countdown--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    
    private void endMinigame(Minigame game, String reason) {
        Player winner = game.calculateWinner();
        
        EBMinigameEndEvent endEvent = new EBMinigameEndEvent(
            game.getId(),
            winner,
            game.getPlayers(),
            reason
        );
        
        api.fireEvent(endEvent);
        
        game.broadcast(ChatColor.GOLD + "=== GAME OVER ===");
        if (winner != null) {
            game.broadcast(ChatColor.YELLOW + "Winner: " + winner.getName());
            giveReward(winner, game.getReward());
        }
        
        // Teleport players back
        game.getPlayers().forEach(p -> p.teleport(game.getLobbySpawn()));
        
        activeGames.remove(game.getId());
    }
    
    private void giveReward(Player player, double reward) {
        // Give rewards
    }
}
```

---

## 8. Custom Admin Tools

### Scenario: Advanced moderation with EventBundler

```java
public class AdminToolsIntegration implements Listener {
    
    private final Map<UUID, List<String>> playerWarnings = new HashMap<>();
    private final int MAX_WARNINGS = 3;
    
    @EventHandler
    public void onPlayerWarn(EBPlayerWarnEvent event) {
        Player player = event.getPlayer();
        String reason = event.getReason();
        Player warner = event.getWarner();
        
        List<String> warnings = playerWarnings.computeIfAbsent(
            player.getUniqueId(),
            k -> new ArrayList<>()
        );
        
        warnings.add(reason);
        
        player.sendMessage(ChatColor.RED + "⚠ WARNING ⚠");
        player.sendMessage(ChatColor.YELLOW + "Reason: " + reason);
        player.sendMessage(ChatColor.YELLOW + "Warnings: " + warnings.size() + "/" + MAX_WARNINGS);
        
        // Auto-kick at max warnings
        if (warnings.size() >= MAX_WARNINGS) {
            EBPlayerKickEvent kickEvent = new EBPlayerKickEvent(
                player,
                warner,
                "Maximum warnings reached"
            );
            
            EventBundler.getApi().fireEvent(kickEvent);
            
            if (!kickEvent.isCancelled()) {
                player.kickPlayer(ChatColor.RED + "Kicked: Too many warnings");
            }
        }
    }
    
    @EventHandler
    public void onPlayerBan(EBPlayerBanEvent event) {
        Player target = event.getTarget();
        Player banner = event.getBanner();
        String reason = event.getReason();
        long duration = event.getDuration();
        
        // Log to database
        logPunishment("BAN", target, banner, reason, duration);
        
        // Notify online staff
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.hasPermission("admin.notifications"))
            .forEach(staff -> staff.sendMessage(
                ChatColor.RED + "[BAN] " + banner.getName() + " banned " + 
                target.getName() + " for " + reason
            ));
        
        // Execute ban
        if (!event.isCancelled()) {
            Bukkit.getBanList(BanList.Type.NAME).addBan(
                target.getName(),
                reason,
                duration > 0 ? new Date(System.currentTimeMillis() + duration) : null,
                banner.getName()
            );
            
            target.kickPlayer(ChatColor.RED + "Banned: " + reason);
        }
    }
    
    @EventHandler
    public void onPlayerMute(EBPlayerMuteEvent event) {
        Player target = event.getTarget();
        long duration = event.getDuration();
        
        // Store mute data
        MuteData mute = new MuteData(target.getUniqueId(), duration);
        saveMuteData(mute);
        
        target.sendMessage(ChatColor.RED + "You have been muted for " + 
            formatDuration(duration));
    }
    
    @EventHandler
    public void onStaffModeToggle(EBStaffModeToggleEvent event) {
        Player staff = event.getPlayer();
        boolean enabled = event.isEnabled();
        
        if (enabled) {
            // Give staff items
            giveStaffItems(staff);
            
            // Make invisible to players
            staff.addPotionEffect(new PotionEffect(
                PotionEffectType.INVISIBILITY,
                Integer.MAX_VALUE,
                1,
                false,
                false
            ));
            
            // Enable flight
            staff.setAllowFlight(true);
            staff.setFlying(true);
            
            staff.sendMessage(ChatColor.GREEN + "Staff mode enabled");
        } else {
            // Remove staff items
            staff.getInventory().clear();
            
            // Remove invisibility
            staff.removePotionEffect(PotionEffectType.INVISIBILITY);
            
            // Disable flight
            staff.setAllowFlight(false);
            staff.setFlying(false);
            
            staff.sendMessage(ChatColor.RED + "Staff mode disabled");
        }
    }
    
    private void logPunishment(String type, Player target, Player issuer, 
                               String reason, long duration) {
        // Database logging
    }
    
    private void saveMuteData(MuteData mute) {
        // Save mute
    }
    
    private String formatDuration(long millis) {
        // Format duration
        return "X minutes";
    }
    
    private void giveStaffItems(Player staff) {
        // Give staff items
    }
}
```

---

## 9. Achievement System

### Scenario: Track player achievements with custom events

```java
public class AchievementSystem implements Listener {
    
    private final Map<UUID, Set<String>> unlockedAchievements = new HashMap<>();
    
    @EventHandler
    public void onAchievementUnlock(EBAchievementUnlockEvent event) {
        Player player = event.getPlayer();
        String achievementId = event.getAchievementId();
        
        Set<String> achievements = unlockedAchievements.computeIfAbsent(
            player.getUniqueId(),
            k -> new HashSet<>()
        );
        
        if (achievements.contains(achievementId)) {
            event.setCancelled(true);
            return;
        }
        
        achievements.add(achievementId);
        
        // Visual effects
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.TOTEM, player.getLocation(), 50);
        
        // Send title
        Achievement achievement = getAchievement(achievementId);
        player.sendTitle(
            ChatColor.GOLD + "Achievement Unlocked!",
            ChatColor.YELLOW + achievement.getName(),
            10, 70, 20
        );
        
        // Broadcast rare achievements
        if (achievement.getRarity() == Rarity.LEGENDARY) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "[LEGENDARY] " + 
                player.getName() + " unlocked " + achievement.getName() + "!");
        }
        
        // Give rewards
        giveAchievementRewards(player, achievement);
        
        // Check for meta-achievements
        if (achievements.size() == 10) {
            unlockAchievement(player, "collector_10");
        } else if (achievements.size() == 50) {
            unlockAchievement(player, "collector_50");
        }
    }
    
    @EventHandler
    public void onPlayerLevelUp(EBPlayerLevelUpEvent event) {
        // Achievement: Reach level milestones
        int level = event.getNewLevel();
        
        if (level == 10) {
            unlockAchievement(event.getPlayer(), "level_10");
        } else if (level == 50) {
            unlockAchievement(event.getPlayer(), "level_50");
        } else if (level == 100) {
            unlockAchievement(event.getPlayer(), "level_100");
        }
    }
    
    @EventHandler
    public void onMoneyTransfer(EBMoneyTransferEvent event) {
        // Achievement: First transaction
        if (!hasAchievement(event.getSender(), "first_transaction")) {
            unlockAchievement(event.getSender(), "first_transaction");
        }
        
        // Achievement: Transfer large amount
        if (event.getAmount() >= 100000) {
            unlockAchievement(event.getSender(), "big_spender");
        }
    }
    
    @EventHandler
    public void onQuestComplete(EBQuestCompleteEvent event) {
        // Achievement: Quest milestones
        PlayerQuestData data = getQuestData(event.getPlayer());
        int completed = data.getCompletedCount();
        
        if (completed == 1) {
            unlockAchievement(event.getPlayer(), "first_quest");
        } else if (completed == 10) {
            unlockAchievement(event.getPlayer(), "quest_veteran");
        } else if (completed == 100) {
            unlockAchievement(event.getPlayer(), "quest_master");
        }
    }
    
    private void unlockAchievement(Player player, String achievementId) {
        EBAchievementUnlockEvent event = new EBAchievementUnlockEvent(
            player,
            achievementId
        );
        EventBundler.getApi().fireEvent(event);
    }
    
    private boolean hasAchievement(Player player, String achievementId) {
        Set<String> achievements = unlockedAchievements.get(player.getUniqueId());
        return achievements != null && achievements.contains(achievementId);
    }
    
    private Achievement getAchievement(String id) {
        // Get achievement data
        return null;
    }
    
    private void giveAchievementRewards(Player player, Achievement achievement) {
        // Give rewards
    }
    
    private PlayerQuestData getQuestData(Player player) {
        // Get quest data
        return null;
    }
}
```

---

## 10. Advanced Automation

### Scenario: Automatic server management with events

```java
public class ServerAutomation implements Listener {
    
    private final EventBundlerAPI api = EventBundler.getApi();
    
    @EventHandler
    public void onPlayerFirstJoin(EBPlayerFirstJoinEvent event) {
        Player player = event.getPlayer();
        
        // Give starter kit
        if (event.isGiveStarterKit()) {
            giveStarterKit(player);
        }
        
        // Teleport to spawn
        player.teleport(event.getSpawnLocation());
        
        // Welcome message
        Bukkit.broadcastMessage(event.getWelcomeMessage()
            .replace("{player}", player.getName()));
        
        // Welcome title
        player.sendTitle(
            ChatColor.GOLD + "Welcome!",
            ChatColor.YELLOW + "Type /help to get started",
            10, 70, 20
        );
        
        // Register for tutorial quest
        startTutorialQuest(player);
    }
    
    @EventHandler
    public void onResetRegion(EBResetRegionEvent event) {
        String regionId = event.getRegionId();
        World world = event.getWorld();
        
        // Clear entities
        clearEntities(world, regionId);
        
        // Reset blocks
        resetBlocks(world, regionId);
        
        // Respawn loot chests
        respawnChests(world, regionId);
        
        // Broadcast reset
        Bukkit.broadcastMessage(ChatColor.YELLOW + 
            "Region '" + regionId + "' has been reset!");
    }
    
    @EventHandler
    public void onServerCustom(EBServerCustomEvent event) {
        String eventType = event.getEventType();
        
        switch (eventType) {
            case "auto_save":
                saveAllData();
                break;
                
            case "auto_restart_warning":
                int minutes = event.getIntData("minutes");
                Bukkit.broadcastMessage(ChatColor.RED + 
                    "Server restart in " + minutes + " minutes!");
                break;
                
            case "lag_detected":
                handleLagSpike();
                break;
                
            case "backup_complete":
                getLogger().info("Backup completed successfully");
                break;
        }
    }
    
    @EventHandler
    public void onCooldownExpire(EBCooldownExpireEvent event) {
        Player player = event.getPlayer();
        String ability = event.getCooldownId();
        
        // Notify player
        player.sendMessage(ChatColor.GREEN + 
            "✓ " + ability + " is ready!");
        
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
        
        // Auto-cast if enabled
        if (player.hasPermission("auto." + ability)) {
            castAbility(player, ability);
        }
    }
    
    @EventHandler
    public void onLogExport(EBLogExportEvent event) {
        String exportPath = event.getExportPath();
        Set<UUID> affectedPlayers = event.getAffectedPlayers();
        
        // Compress logs
        compressLogs(exportPath);
        
        // Notify admins
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.hasPermission("admin.logs"))
            .forEach(admin -> admin.sendMessage(
                ChatColor.GREEN + "Logs exported to: " + exportPath
            ));
    }
    
    private void giveStarterKit(Player player) {
        // Give starting items
    }
    
    private void startTutorialQuest(Player player) {
        // Start tutorial
    }
    
    private void clearEntities(World world, String regionId) {
        // Clear entities in region
    }
    
    private void resetBlocks(World world, String regionId) {
        // Reset blocks
    }
    
    private void respawnChests(World world, String regionId) {
        // Respawn loot
    }
    
    private void saveAllData() {
        // Save all plugin data
    }
    
    private void handleLagSpike() {
        // Reduce particles, clear items, etc.
    }
    
    private void castAbility(Player player, String ability) {
        // Cast ability
    }
    
    private void compressLogs(String path) {
        // Compress log files
    }
}
```

---

## Quick Reference: All Event Names

```
Transfer (10):
- eb-transfer
- eb-transfer-cancel
- eb-money-transfer
- eb-item-transfer
- eb-data-transfer
- eb-permission-transfer
- eb-rank-transfer
- eb-ownership-transfer
- eb-inventory-transfer
- eb-experience-transfer

Player (15):
- eb-player-level-up
- eb-player-rank-change
- eb-player-first-join
- eb-player-afk
- eb-player-afk-return
- eb-player-nickname-change
- eb-player-teleport-request
- eb-player-home-set
- eb-player-warp-create
- eb-player-gamemode-change
- eb-player-fly-toggle
- eb-player-vanish
- eb-player-speed-change
- eb-player-god-mode
- eb-player-inventory-sort

Combat (10):
- eb-combat-start
- eb-combat-end
- eb-combo-hit
- eb-critical-hit
- eb-dodge
- eb-parry
- eb-reflect-damage
- eb-execute
- eb-killstreak
- eb-duel-start

Economy (10):
- eb-economy-transaction
- eb-shop-purchase
- eb-shop-sell
- eb-auction-start
- eb-auction-bid
- eb-auction-win
- eb-trade-request
- eb-trade-complete
- eb-loan-request
- eb-payment-complete

[... and 70 more events across 6 other categories]
```

---

*EventBundler v1.0.0 - 110 Events for Infinite Possibilities*