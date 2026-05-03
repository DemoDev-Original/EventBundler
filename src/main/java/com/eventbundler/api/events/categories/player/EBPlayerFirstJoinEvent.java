package com.eventbundler.api.events.categories.player;

import com.eventbundler.api.events.CustomBukkitEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
public class EBPlayerFirstJoinEvent extends CustomBukkitEvent {
    
    private final Player player;
    private final long timestamp;
    
    @Setter
    private Location spawnLocation;
    
    @Setter
    private String welcomeMessage;
    
    @Setter
    private boolean giveStarterKit;
    
    public EBPlayerFirstJoinEvent(Player player, Location spawnLocation, String welcomeMessage) {
        super("eb-player-first-join", "player");
        this.player = player;
        this.timestamp = System.currentTimeMillis();
        this.spawnLocation = spawnLocation;
        this.welcomeMessage = welcomeMessage;
        this.giveStarterKit = true;
    }
}