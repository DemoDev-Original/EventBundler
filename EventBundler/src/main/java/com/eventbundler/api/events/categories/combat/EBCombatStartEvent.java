package com.eventbundler.api.events.categories.combat;

import com.eventbundler.api.events.CancellableCustomEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@Getter
public class EBCombatStartEvent extends CancellableCustomEvent {
    
    private final Player player;
    private final Entity opponent;
    private final CombatType combatType;
    
    @Setter
    private int combatDuration;
    
    @Setter
    private boolean allowFlight;
    
    public EBCombatStartEvent(Player player, Entity opponent, CombatType combatType, int combatDuration) {
        super("eb-combat-start", "combat");
        this.player = player;
        this.opponent = opponent;
        this.combatType = combatType;
        this.combatDuration = combatDuration;
        this.allowFlight = false;
    }
    
    public enum CombatType {
        PVP,
        PVE,
        DUEL,
        ARENA,
        CUSTOM
    }
}