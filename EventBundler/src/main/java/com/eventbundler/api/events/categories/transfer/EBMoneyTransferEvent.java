package com.eventbundler.api.events.categories.transfer;

import com.eventbundler.api.events.CancellableCustomEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
public class EBMoneyTransferEvent extends CancellableCustomEvent {
    
    private final Player sender;
    private final Player receiver;
    
    @Setter
    private double amount;
    
    private final String currency;
    private final TransferReason reason;
    
    public EBMoneyTransferEvent(Player sender, Player receiver, double amount, String currency, TransferReason reason) {
        super("eb-money-transfer", "transfer");
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.currency = currency;
        this.reason = reason;
    }
    
    public enum TransferReason {
        TRADE,
        PAYMENT,
        GIFT,
        LOAN,
        REFUND,
        REWARD,
        CUSTOM
    }
}