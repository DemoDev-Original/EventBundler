package com.eventbundler.api;

import com.eventbundler.EventBundler;
import com.eventbundler.api.events.CustomBukkitEvent;
import com.eventbundler.manager.EventManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class EventBundlerAPI {
    
    private final EventBundler plugin;
    private final EventManager eventManager;
    
    public EventBundlerAPI(EventBundler plugin) {
        this.plugin = plugin;
        this.eventManager = plugin.getEventManager();
    }
    
    public <T extends CustomBukkitEvent> T fireEvent(T event) {
        if (!isEventEnabled(event.getEventName())) {
            event.setCancelled(true);
            return event;
        }
        
        if (plugin.getConfigManager().isDebugEnabled()) {
            plugin.getLogger().info("[API] Firing event: " + event.getEventName());
        }
        
        Bukkit.getPluginManager().callEvent(event);
        eventManager.trackEvent(event);
        
        if (plugin.getConfigManager().shouldLogEvents()) {
            plugin.getLogger().info("[EVENT] " + event.getEventName() + " fired by " + event.getClass().getSimpleName());
        }
        
        return event;
    }
    
    public <T extends Event> T fireVanillaEvent(T event) {
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }
    
    public boolean isEventEnabled(String eventName) {
        return eventManager.isEventEnabled(eventName);
    }
    
    public boolean isCategoryEnabled(String category) {
        return eventManager.isCategoryEnabled(category);
    }
    
    public List<String> getEnabledEvents() {
        return eventManager.getEnabledEvents();
    }
    
    public List<String> getEnabledCategories() {
        return eventManager.getEnabledCategories();
    }
    
    public Map<String, Long> getEventStatistics() {
        return eventManager.getEventStatistics();
    }
    
    public long getEventFireCount(String eventName) {
        return eventManager.getEventFireCount(eventName);
    }
    
    public void resetStatistics() {
        eventManager.resetStatistics();
    }
    
    public void toggleEvent(String eventName, boolean enabled) {
        eventManager.toggleEvent(eventName, enabled);
    }
    
    public void toggleCategory(String category, boolean enabled) {
        eventManager.toggleCategory(category, enabled);
    }
    
    public boolean hasPlayerListener(UUID playerId, String eventName) {
        return eventManager.hasPlayerListener(playerId, eventName);
    }
    
    public void registerPlayerListener(UUID playerId, String eventName) {
        eventManager.registerPlayerListener(playerId, eventName);
    }
    
    public void unregisterPlayerListener(UUID playerId, String eventName) {
        eventManager.unregisterPlayerListener(playerId, eventName);
    }
    
    public String getApiVersion() {
        return plugin.getDescription().getVersion();
    }
    
    public boolean isDebugMode() {
        return plugin.getConfigManager().isDebugEnabled();
    }
}