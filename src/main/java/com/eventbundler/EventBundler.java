package com.eventbundler;

import com.eventbundler.api.EventBundlerAPI;
import com.eventbundler.commands.EventBundlerCommand;
import com.eventbundler.examples.ExampleListener;
import com.eventbundler.manager.ConfigManager;
import com.eventbundler.manager.EventManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class EventBundler extends JavaPlugin {
    
    private static EventBundler instance;
    
    private EventManager eventManager;
    private ConfigManager configManager;
    private EventBundlerAPI api;
    
    @Override
    public void onEnable() {
        instance = this;
        
        long startTime = System.currentTimeMillis();
        
        saveDefaultConfig();
        
        this.configManager = new ConfigManager(this);
        this.eventManager = new EventManager(this);
        this.api = new EventBundlerAPI(this);
        
        registerCommands();
        registerExamples();
        
        long loadTime = System.currentTimeMillis() - startTime;
        
        getLogger().info("=======================================");
        getLogger().info("  EventBundler v" + getDescription().getVersion());
        getLogger().info("  Loaded 110 custom events");
        getLogger().info("  API Status: " + configManager.getApiAccess());
        getLogger().info("  Load Time: " + loadTime + "ms");
        getLogger().info("=======================================");
        
        if (configManager.isDebugEnabled()) {
            getLogger().info("[DEBUG] Debug mode enabled");
            getLogger().info("[DEBUG] Enabled categories: " + eventManager.getEnabledCategoriesCount());
            getLogger().info("[DEBUG] Enabled events: " + eventManager.getEnabledEventsCount());
        }
    }
    
    @Override
    public void onDisable() {
        if (eventManager != null) {
            eventManager.shutdown();
        }
        
        getLogger().info("EventBundler disabled successfully");
    }
    
    private void registerCommands() {
        EventBundlerCommand mainCommand = new EventBundlerCommand(this);
        getCommand("eventbundler").setExecutor(mainCommand);
        getCommand("eventbundler").setTabCompleter(mainCommand);
    }
    
    private void registerExamples() {
        if (configManager.isDebugEnabled()) {
            Bukkit.getPluginManager().registerEvents(new ExampleListener(this), this);
            getLogger().info("[DEBUG] Example listener registered");
        }
    }
    
    public void reload() {
        reloadConfig();
        configManager = new ConfigManager(this);
        eventManager.reload();
        getLogger().info("EventBundler reloaded successfully");
    }
    
    public static EventBundler getInstance() {
        return instance;
    }
    
    public static EventBundlerAPI getApi() {
        return instance != null ? instance.api : null;
    }
}