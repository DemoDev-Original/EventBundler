package com.eventbundler.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public abstract class CustomBukkitEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private final String eventName;
    private final String category;
    private final long timestamp;
    
    @Setter
    private boolean cancelled;
    
    public CustomBukkitEvent(String eventName, String category) {
        this.eventName = eventName;
        this.category = category;
        this.timestamp = System.currentTimeMillis();
        this.cancelled = false;
    }
    
    public CustomBukkitEvent(String eventName, String category, boolean async) {
        super(async);
        this.eventName = eventName;
        this.category = category;
        this.timestamp = System.currentTimeMillis();
        this.cancelled = false;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    public boolean isCancellable() {
        return false;
    }
}