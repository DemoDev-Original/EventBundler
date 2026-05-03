# EventBundler API Documentation

## Version 1.0.0
**Compatible with:** Minecraft 1.12 - 1.21+

---

## Table of Contents
1. [Getting Started](#getting-started)
2. [Core API](#core-api)
3. [Event Categories](#event-categories)
4. [Creating Custom Events](#creating-custom-events)
5. [Listening to Events](#listening-to-events)
6. [Firing Events](#firing-events)
7. [Advanced Usage](#advanced-usage)
8. [Best Practices](#best-practices)

---

## Getting Started

### Adding EventBundler as a Dependency

**Maven:**
```xml
<dependency>
    <groupId>com.eventbundler</groupId>
    <artifactId>EventBundler</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

**Gradle:**
```gradle
dependencies {
    compileOnly 'com.eventbundler:EventBundler:1.0.0'
}
```

### plugin.yml Configuration
```yaml
depend: [EventBundler]
```

---

## Core API

### Accessing the API

```java
import com.eventbundler.EventBundler;
import com.eventbundler.api.EventBundlerAPI;

// Method 1: Static access
EventBundlerAPI api = EventBundler.getApi();

// Method 2: Plugin instance
EventBundler plugin = (EventBundler) Bukkit.getPluginManager().getPlugin("EventBundler");
EventBundlerAPI api = plugin.getApi();
```

### API Methods

```java
// Fire an event
<T extends CustomBukkitEvent> T fireEvent(T event)

// Check if event is enabled
boolean isEventEnabled(String eventName)

// Check if category is enabled
boolean isCategoryEnabled(String category)

// Get enabled events
List<String> getEnabledEvents()

// Get event statistics
Map<String, Long> getEventStatistics()
long getEventFireCount(String eventName)

// Toggle events
void toggleEvent(String eventName, boolean enabled)
void toggleCategory(String category, boolean enabled)

// Player listeners
boolean hasPlayerListener(UUID playerId, String eventName)
void registerPlayerListener(UUID playerId, String eventName)
void unregisterPlayerListener(UUID playerId, String eventName)
```

---

## Event Categories

EventBundler provides 110 events across 10 categories:

### 1. Transfer Events (10 events)
- **EBTransferEvent** - Generic transfer event
- **EBTransferCancelEvent** - Transfer cancellation
- **EBMoneyTransferEvent** - Money transfers
- **EBItemTransferEvent** - Item transfers
- And 6 more...

### 2. Player Events (15 events)
- **EBPlayerLevelUpEvent** - Player level progression
- **EBPlayerFirstJoinEvent** - First-time join
- **EBPlayerAFKEvent** - AFK detection
- And 12 more...

### 3. Block Events (12 events)
- **EBBlockMultiBreakEvent** - Multiple block breaking
- **EBBlockTransformEvent** - Block transformations
- And 10 more...

### 4. Entity Events (12 events)
- **EBEntityLevelUpEvent** - Entity progression
- **EBEntityEvolutionEvent** - Entity evolution
- And 10 more...

### 5. Combat Events (10 events)
- **EBCombatStartEvent** - Combat initiation
- **EBComboHitEvent** - Combo attacks
- **EBCriticalHitEvent** - Critical strikes
- And 7 more...

### 6. Economy Events (10 events)
- **EBShopPurchaseEvent** - Shop transactions
- **EBAuctionBidEvent** - Auction interactions
- **EBTradeCompleteEvent** - Player trading
- And 7 more...

### 7. Social Events (10 events)
- **EBPartyCreateEvent** - Party creation
- **EBFriendRequestEvent** - Friend system
- **EBGuildCreateEvent** - Guild management
- And 7 more...

### 8. World Events (10 events)
- **EBChunkGenerateCustomEvent** - Custom chunk generation
- **EBWeatherChangeCustomEvent** - Weather control
- And 8 more...

### 9. Admin Events (8 events)
- **EBPlayerBanEvent** - Ban system
- **EBPlayerKickEvent** - Kick actions
- **EBReportSubmitEvent** - Report system
- And 5 more...

### 10. Custom Events (13 events)
- **EBQuestStartEvent** - Quest system
- **EBMinigameJoinEvent** - Minigame framework
- **EBSkillUseEvent** - Skill systems
- And 10 more...

---

## Creating Custom Events

### Basic Custom Event

```java
import com.eventbundler.api.events.CustomBukkitEvent;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class MyCustomEvent extends CustomBukkitEvent {
    
    private final Player player;
    private final String data;
    
    public MyCustomEvent(Player player, String data) {
        super("my-custom-event", "custom");
        this.player = player;
        this.data = data;
    }
}
```

### Cancellable Custom Event

```java
import com.eventbundler.api.events.CancellableCustomEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
public class MyCancellableEvent extends CancellableCustomEvent {
    
    private final Player player;
    
    @Setter
    private double value;
    
    public MyCancellableEvent(Player player, double value) {
        super("my-cancellable-event", "custom");
        this.player = player;
        this.value = value;
    }
}
```

### Async Event

```java
public class MyAsyncEvent extends CustomBukkitEvent {
    
    public MyAsyncEvent() {
        super("my-async-event", "custom", true); // true = async
    }
}
```

---

## Listening to Events

### Basic Event Listener

```java
import com.eventbundler.api.events.categories.transfer.EBTransferEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MyListener implements Listener {
    
    @EventHandler
    public void onTransfer(EBTransferEvent event) {
        Player sender = event.getSender();
        Player receiver = event.getReceiver();
        double amount = event.getAmount();
        
        // Your logic here
        if (amount > 1000) {
            event.setCancelled(true);
            sender.sendMessage("Transfer amount too large!");
        }
    }
}
```

### Priority and Cancellation

```java
import org.bukkit.event.EventPriority;

@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
public void onPlayerLevelUp(EBPlayerLevelUpEvent event) {
    // This runs at HIGH priority and ignores cancelled events
    
    int newLevel = event.getNewLevel();
    if (newLevel > 100) {
        event.setNewLevel(100); // Cap at level 100
    }
}
```

### Multiple Events

```java
@EventHandler
public void onTransfer(EBTransferEvent event) {
    // Handle generic transfers
}

@EventHandler
public void onMoneyTransfer(EBMoneyTransferEvent event) {
    // Handle money-specific transfers
}

@EventHandler
public void onItemTransfer(EBItemTransferEvent event) {
    // Handle item-specific transfers
}
```

---

## Firing Events

### Basic Event Firing

```java
import com.eventbundler.EventBundler;
import com.eventbundler.api.events.categories.transfer.EBMoneyTransferEvent;

public void transferMoney(Player sender, Player receiver, double amount) {
    EBMoneyTransferEvent event = new EBMoneyTransferEvent(
        sender,
        receiver,
        amount,
        "USD",
        EBMoneyTransferEvent.TransferReason.TRADE
    );
    
    // Fire the event
    EventBundler.getApi().fireEvent(event);
    
    // Check if cancelled
    if (event.isCancelled()) {
        sender.sendMessage("Transfer was cancelled!");
        return;
    }
    
    // Process the transfer
    double finalAmount = event.getAmount(); // May have been modified
    // ... your transfer logic
}
```

### With Event Response

```java
public void levelUpPlayer(Player player, int newLevel) {
    EBPlayerLevelUpEvent event = new EBPlayerLevelUpEvent(
        player,
        player.getLevel(),
        newLevel,
        "MyLevelSystem",
        100.0 // reward
    );
    
    EventBundler.getApi().fireEvent(event);
    
    if (!event.isCancelled()) {
        // Event modified values
        int finalLevel = event.getNewLevel();
        double finalReward = event.getReward();
        
        player.setLevel(finalLevel);
        // Give reward...
    }
}
```

### Fire and Forget

```java
// For events that don't need response checking
EventBundler.getApi().fireEvent(new EBPlayerFirstJoinEvent(
    player,
    player.getLocation(),
    "Welcome to the server!"
));
```

---

## Advanced Usage

### 1. Event Statistics Tracking

```java
EventBundlerAPI api = EventBundler.getApi();

// Get total fires for an event
long transferCount = api.getEventFireCount("eb-transfer");

// Get all statistics
Map<String, Long> stats = api.getEventStatistics();
stats.forEach((event, count) -> {
    System.out.println(event + ": " + count + " fires");
});

// Reset statistics
api.resetStatistics();
```

### 2. Dynamic Event Control

```java
// Disable an event at runtime
api.toggleEvent("eb-money-transfer", false);

// Disable entire category
api.toggleCategory("combat", false);

// Check status
if (api.isEventEnabled("eb-transfer")) {
    // Event is active
}
```

### 3. Player-Specific Listeners

```java
UUID playerId = player.getUniqueId();

// Register player for specific event tracking
api.registerPlayerListener(playerId, "eb-shop-purchase");

// Check if registered
if (api.hasPlayerListener(playerId, "eb-shop-purchase")) {
    // Player is tracked
}

// Unregister
api.unregisterPlayerListener(playerId, "eb-shop-purchase");
```

### 4. Cross-Plugin Integration

```java
public class MyEconomyPlugin extends JavaPlugin {
    
    private EventBundlerAPI eventAPI;
    
    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().isPluginEnabled("EventBundler")) {
            eventAPI = EventBundler.getApi();
            getLogger().info("EventBundler integration enabled!");
        }
    }
    
    public void processTransaction(Player buyer, Player seller, double amount) {
        if (eventAPI != null && eventAPI.isEventEnabled("eb-economy-transaction")) {
            // Fire custom economy event
            EBEconomyTransactionEvent event = new EBEconomyTransactionEvent(
                buyer, seller, amount
            );
            eventAPI.fireEvent(event);
            
            if (event.isCancelled()) {
                return;
            }
        }
        
        // Continue with transaction...
    }
}
```

### 5. Event Chaining

```java
@EventHandler
public void onTransfer(EBTransferEvent event) {
    if (event.getType() == EBTransferEvent.TransferType.MONEY) {
        // Fire specific money transfer event
        EBMoneyTransferEvent moneyEvent = new EBMoneyTransferEvent(
            event.getSender(),
            event.getReceiver(),
            event.getAmount(),
            "USD",
            EBMoneyTransferEvent.TransferReason.CUSTOM
        );
        
        EventBundler.getApi().fireEvent(moneyEvent);
        
        // If money event was cancelled, cancel parent
        if (moneyEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
```

---

## Best Practices

### 1. Always Check for Cancellation

```java
MyEvent event = new MyEvent(player);
EventBundler.getApi().fireEvent(event);

if (event.isCancelled()) {
    // Don't proceed
    return;
}

// Safe to continue
```

### 2. Use Event Priority Wisely

```java
@EventHandler(priority = EventPriority.LOWEST)
public void earlyCheck(EBTransferEvent event) {
    // Validation logic
}

@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
public void logTransfer(EBTransferEvent event) {
    // Logging only - don't modify
}
```

### 3. Minimize Event Data Modification

```java
@EventHandler
public void onLevelUp(EBPlayerLevelUpEvent event) {
    // ✓ Good: Only modify if necessary
    if (event.getNewLevel() > getMaxLevel()) {
        event.setNewLevel(getMaxLevel());
    }
    
    // ✗ Bad: Unnecessary modifications
    event.setReward(event.getReward()); // No-op
}
```

### 4. Use Appropriate Event Types

```java
// ✓ Good: Specific events
EventBundler.getApi().fireEvent(new EBMoneyTransferEvent(...));

// ✗ Bad: Generic when specific exists
EventBundler.getApi().fireEvent(new EBTransferEvent(...));
```

### 5. Handle Async Events Properly

```java
@EventHandler
public void onAsyncEvent(MyAsyncEvent event) {
    // ✓ Good: Async-safe operations
    Database.saveAsync(event.getData());
    
    // ✗ Bad: Bukkit API calls (not thread-safe)
    event.getPlayer().sendMessage("Hello"); // Dangerous!
    
    // ✓ Good: Schedule sync task
    Bukkit.getScheduler().runTask(plugin, () -> {
        event.getPlayer().sendMessage("Hello");
    });
}
```

### 6. Document Custom Events

```java
/**
 * Fired when a player completes a custom quest.
 * 
 * This event is cancellable. If cancelled, rewards are not given.
 * 
 * @see QuestManager
 */
@Getter
public class EBQuestCompleteEvent extends CancellableCustomEvent {
    // ...
}
```

---

## Example: Complete Integration

```java
package com.example.myplugin;

import com.eventbundler.EventBundler;
import com.eventbundler.api.EventBundlerAPI;
import com.eventbundler.api.events.categories.transfer.EBMoneyTransferEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin implements Listener {
    
    private EventBundlerAPI eventAPI;
    
    @Override
    public void onEnable() {
        if (!setupEventBundler()) {
            getLogger().warning("EventBundler not found - limited functionality");
            return;
        }
        
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("EventBundler integration complete!");
    }
    
    private boolean setupEventBundler() {
        if (!getServer().getPluginManager().isPluginEnabled("EventBundler")) {
            return false;
        }
        
        eventAPI = EventBundler.getApi();
        return eventAPI != null;
    }
    
    // Fire events from your plugin
    public void payPlayer(Player from, Player to, double amount) {
        if (eventAPI == null || !eventAPI.isEventEnabled("eb-money-transfer")) {
            // Fallback logic
            processDirectPayment(from, to, amount);
            return;
        }
        
        EBMoneyTransferEvent event = new EBMoneyTransferEvent(
            from, to, amount, "GOLD",
            EBMoneyTransferEvent.TransferReason.PAYMENT
        );
        
        eventAPI.fireEvent(event);
        
        if (event.isCancelled()) {
            from.sendMessage("Payment was blocked!");
            return;
        }
        
        processDirectPayment(from, to, event.getAmount());
    }
    
    // Listen to EventBundler events
    @EventHandler
    public void onMoneyTransfer(EBMoneyTransferEvent event) {
        // Log to your system
        logTransaction(
            event.getSender().getName(),
            event.getReceiver().getName(),
            event.getAmount()
        );
        
        // Apply tax
        if (event.getAmount() > 1000) {
            double tax = event.getAmount() * 0.05;
            event.setAmount(event.getAmount() - tax);
            event.getSender().sendMessage("5% tax applied: $" + tax);
        }
    }
    
    private void processDirectPayment(Player from, Player to, double amount) {
        // Your economy logic
    }
    
    private void logTransaction(String from, String to, double amount) {
        // Your logging logic
    }
}
```

---

## Support

- **Documentation:** This file
- **Examples:** See `com.eventbundler.examples` package
- **Configuration:** `config.yml` with inline comments
- **Commands:** `/eb help` in-game

---

*EventBundler v1.0.0 - Compatible with Minecraft 1.12 to 1.21+*