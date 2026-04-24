package com.eventbundler.api.events;

import org.bukkit.event.Cancellable;

public abstract class CancellableCustomEvent extends CustomBukkitEvent implements Cancellable {
    
    public CancellableCustomEvent(String eventName, String category) {
        super(eventName, category);
    }
    
    public CancellableCustomEvent(String eventName, String category, boolean async) {
        super(eventName, category, async);
    }
    
    @Override
    public boolean isCancellable() {
        return true;
    }
}