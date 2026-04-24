package com.eventbundler.api.events.categories.transfer;

import com.eventbundler.api.events.CancellableCustomEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
public class EBItemTransferEvent extends CancellableCustomEvent {
    
    private final Player sender;
    private final Player receiver;
    
    @Setter
    private List<ItemStack> items;
    
    private final TransferMethod method;
    
    public EBItemTransferEvent(Player sender, Player receiver, List<ItemStack> items, TransferMethod method) {
        super("eb-item-transfer", "transfer");
        this.sender = sender;
        this.receiver = receiver;
        this.items = items;
        this.method = method;
    }
    
    public enum TransferMethod {
        TRADE,
        DROP_PICKUP,
        COMMAND,
        SHOP,
        CHEST,
        MAIL,
        CUSTOM
    }
}