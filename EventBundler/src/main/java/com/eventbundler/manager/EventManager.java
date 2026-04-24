package com.eventbundler.manager;

import com.eventbundler.EventBundler;
import com.eventbundler.api.events.CustomBukkitEvent;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EventManager {
    
    private final EventBundler plugin;
    private final ConfigManager configManager;
    
    @Getter
    private final Map<String, Long> eventStatistics;
    
    private final Map<UUID, Set<String>> playerListeners;
    
    public EventManager(EventBundler plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.eventStatistics = new ConcurrentHashMap<>();
        this.playerListeners = new ConcurrentHashMap<>();
    }
    
    public void trackEvent(CustomBukkitEvent event) {
        String eventName = event.getEventName();
        eventStatistics.merge(eventName, 1L, Long::sum);
    }
    
    public boolean isEventEnabled(String eventName) {
        String category = getEventCategory(eventName);
        if (!isCategoryEnabled(category)) {
            return false;
        }
        return plugin.getConfig().getBoolean("events." + eventName + ".enabled", true);
    }
    
    public boolean isCategoryEnabled(String category) {
        return plugin.getConfig().getBoolean("categories." + category + ".enabled", true);
    }
    
    public List<String> getEnabledEvents() {
        return plugin.getConfig().getConfigurationSection("events").getKeys(false).stream()
                .filter(this::isEventEnabled)
                .collect(Collectors.toList());
    }
    
    public List<String> getEnabledCategories() {
        return plugin.getConfig().getConfigurationSection("categories").getKeys(false).stream()
                .filter(this::isCategoryEnabled)
                .collect(Collectors.toList());
    }
    
    public int getEnabledEventsCount() {
        return getEnabledEvents().size();
    }
    
    public int getEnabledCategoriesCount() {
        return getEnabledCategories().size();
    }
    
    public long getEventFireCount(String eventName) {
        return eventStatistics.getOrDefault(eventName, 0L);
    }
    
    public void resetStatistics() {
        eventStatistics.clear();
        plugin.getLogger().info("Event statistics reset");
    }
    
    public void toggleEvent(String eventName, boolean enabled) {
        plugin.getConfig().set("events." + eventName + ".enabled", enabled);
        plugin.saveConfig();
        
        if (configManager.isDebugEnabled()) {
            plugin.getLogger().info("[DEBUG] Event " + eventName + " " + (enabled ? "enabled" : "disabled"));
        }
    }
    
    public void toggleCategory(String category, boolean enabled) {
        plugin.getConfig().set("categories." + category + ".enabled", enabled);
        plugin.saveConfig();
        
        if (configManager.isDebugEnabled()) {
            plugin.getLogger().info("[DEBUG] Category " + category + " " + (enabled ? "enabled" : "disabled"));
        }
    }
    
    public boolean hasPlayerListener(UUID playerId, String eventName) {
        Set<String> listeners = playerListeners.get(playerId);
        return listeners != null && listeners.contains(eventName);
    }
    
    public void registerPlayerListener(UUID playerId, String eventName) {
        playerListeners.computeIfAbsent(playerId, k -> ConcurrentHashMap.newKeySet()).add(eventName);
    }
    
    public void unregisterPlayerListener(UUID playerId, String eventName) {
        Set<String> listeners = playerListeners.get(playerId);
        if (listeners != null) {
            listeners.remove(eventName);
            if (listeners.isEmpty()) {
                playerListeners.remove(playerId);
            }
        }
    }
    
    private String getEventCategory(String eventName) {
        if (eventName.contains("transfer")) return "transfer";
        if (eventName.contains("player")) return "player";
        if (eventName.contains("block")) return "block";
        if (eventName.contains("entity")) return "entity";
        if (eventName.contains("combat")) return "combat";
        if (eventName.contains("economy") || eventName.contains("shop") || eventName.contains("auction")) return "economy";
        if (eventName.contains("party") || eventName.contains("friend") || eventName.contains("guild")) return "social";
        if (eventName.contains("chunk") || eventName.contains("world") || eventName.contains("weather")) return "world";
        if (eventName.contains("ban") || eventName.contains("kick") || eventName.contains("mute")) return "admin";
        return "custom";
    }
    
    public void reload() {
        if (configManager.isDebugEnabled()) {
            plugin.getLogger().info("[DEBUG] EventManager reloaded");
        }
    }
    
    public void shutdown() {
        playerListeners.clear();
        if (configManager.isDebugEnabled()) {
            plugin.getLogger().info("[DEBUG] EventManager shutdown - tracked " + eventStatistics.size() + " unique events");
        }
    }
}