package com.eventbundler.api.events.categories.transfer;

import com.eventbundler.api.events.CustomBukkitEvent;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class EBTransferCancelEvent extends CustomBukkitEvent {
    
    private final Player sender;
    private final Player receiver;
    private final EBTransferEvent.TransferType type;
    private final String reason;
    private final Object originalData;
    
    public EBTransferCancelEvent(Player sender, Player receiver, EBTransferEvent.TransferType type, String reason, Object originalData) {
        super("eb-transfer-cancel", "transfer");
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.reason = reason;
        this.originalData = originalData;
    }
}