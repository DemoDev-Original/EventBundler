package com.eventbundler.manager;

import com.eventbundler.EventBundler;
import lombok.Getter;

@Getter
public class ConfigManager {
    
    private final EventBundler plugin;
    
    private final boolean debugEnabled;
    private final boolean logEvents;
    private final boolean autoSave;
    private final String apiAccess;
    
    public ConfigManager(EventBundler plugin) {
        this.plugin = plugin;
        
        this.debugEnabled = plugin.getConfig().getBoolean("settings.debug", false);
        this.logEvents = plugin.getConfig().getBoolean("settings.log-events", false);
        this.autoSave = plugin.getConfig().getBoolean("settings.auto-save", true);
        this.apiAccess = plugin.getConfig().getString("settings.api-access", "open");
    }
    
    public boolean shouldLogEvents() {
        return logEvents;
    }
    
    public boolean isApiOpen() {
        return "open".equalsIgnoreCase(apiAccess);
    }
    
    public boolean isApiRestricted() {
        return "restricted".equalsIgnoreCase(apiAccess);
    }
    
    public boolean isApiDisabled() {
        return "disabled".equalsIgnoreCase(apiAccess);
    }
}