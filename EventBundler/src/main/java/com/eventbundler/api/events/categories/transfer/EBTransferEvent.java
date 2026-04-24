package com.eventbundler.api.events.categories.transfer;

import com.eventbundler.api.events.CancellableCustomEvent;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class EBTransferEvent extends CancellableCustomEvent {
    
    private final Player sender;
    private final Player receiver;
    private final TransferType type;
    private final Object data;
    private final double amount;
    
    public EBTransferEvent(Player sender, Player receiver, TransferType type, Object data, double amount) {
        super("eb-transfer", "transfer");
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.data = data;
        this.amount = amount;
    }
    
    public enum TransferType {
        MONEY,
        ITEMS,
        DATA,
        PERMISSION,
        RANK,
        OWNERSHIP,
        INVENTORY,
        EXPERIENCE
    }
}