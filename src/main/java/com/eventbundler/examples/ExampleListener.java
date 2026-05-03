package com.eventbundler.examples;

import com.eventbundler.EventBundler;
import com.eventbundler.api.events.categories.transfer.EBTransferEvent;
import com.eventbundler.api.events.categories.transfer.EBTransferCancelEvent;
import com.eventbundler.api.events.categories.player.EBPlayerLevelUpEvent;
import com.eventbundler.api.events.categories.combat.EBCombatStartEvent;
import com.eventbundler.api.events.categories.economy.EBShopPurchaseEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ExampleListener implements Listener {
    
    private final EventBundler plugin;
    
    public ExampleListener(EventBundler plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onTransfer(EBTransferEvent event) {
        plugin.getLogger().info("[EXAMPLE] Transfer from " + event.getSender().getName() + 
                " to " + event.getReceiver().getName() + 
                " (Type: " + event.getType() + ", Amount: " + event.getAmount() + ")");
        
        if (event.getAmount() > 10000) {
            event.setCancelled(true);
            event.getSender().sendMessage(ChatColor.RED + "Transfer amount too large!");
            plugin.getLogger().info("[EXAMPLE] Large transfer cancelled");
        }
    }
    
    @EventHandler
    public void onTransferCancel(EBTransferCancelEvent event) {
        plugin.getLogger().info("[EXAMPLE] Transfer cancelled - Reason: " + event.getReason());
        
        if (event.getSender() != null) {
            event.getSender().sendMessage(ChatColor.RED + "Transfer cancelled: " + event.getReason());
        }
    }
    
    @EventHandler
    public void onPlayerLevelUp(EBPlayerLevelUpEvent event) {
        plugin.getLogger().info("[EXAMPLE] " + event.getPlayer().getName() + 
                " leveled up from " + event.getPreviousLevel() + 
                " to " + event.getNewLevel() + 
                " (System: " + event.getLevelSystem() + ")");
        
        event.getPlayer().sendMessage(ChatColor.GREEN + "Congratulations! You reached level " + event.getNewLevel());
        event.getPlayer().sendMessage(ChatColor.GOLD + "Reward: $" + event.getReward());
    }
    
    @EventHandler
    public void onCombatStart(EBCombatStartEvent event) {
        plugin.getLogger().info("[EXAMPLE] Combat started - " + 
                event.getPlayer().getName() + " vs " + event.getOpponent().getName() + 
                " (Type: " + event.getCombatType() + ")");
        
        event.getPlayer().sendMessage(ChatColor.RED + "You are now in combat for " + 
                event.getCombatDuration() + " seconds!");
    }
    
    @EventHandler
    public void onShopPurchase(EBShopPurchaseEvent event) {
        plugin.getLogger().info("[EXAMPLE] " + event.getPlayer().getName() + 
                " purchased from " + event.getShopName() + 
                " - Price: $" + event.getFinalPrice());
        
        if (event.getDiscount() > 0) {
            event.getPlayer().sendMessage(ChatColor.GREEN + "Discount applied: " + 
                    (event.getDiscount() * 100) + "%");
        }
    }
}