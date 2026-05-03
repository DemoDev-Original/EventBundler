package com.eventbundler.api.events.categories.player;

import com.eventbundler.api.events.CancellableCustomEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
public class EBPlayerLevelUpEvent extends CancellableCustomEvent {
    
    private final Player player;
    private final int previousLevel;
    
    @Setter
    private int newLevel;
    
    private final String levelSystem;
    
    @Setter
    private double reward;
    
    public EBPlayerLevelUpEvent(Player player, int previousLevel, int newLevel, String levelSystem, double reward) {
        super("eb-player-level-up", "player");
        this.player = player;
        this.previousLevel = previousLevel;
        this.newLevel = newLevel;
        this.levelSystem = levelSystem;
        this.reward = reward;
    }
}