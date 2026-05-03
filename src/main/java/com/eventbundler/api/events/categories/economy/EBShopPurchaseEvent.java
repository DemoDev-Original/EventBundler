package com.eventbundler.api.events.categories.economy;

import com.eventbundler.api.events.CancellableCustomEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class EBShopPurchaseEvent extends CancellableCustomEvent {
    
    private final Player player;
    private final String shopName;
    private final ItemStack item;
    
    @Setter
    private double price;
    
    private final int quantity;
    
    @Setter
    private double discount;
    
    public EBShopPurchaseEvent(Player player, String shopName, ItemStack item, double price, int quantity) {
        super("eb-shop-purchase", "economy");
        this.player = player;
        this.shopName = shopName;
        this.item = item;
        this.price = price;
        this.quantity = quantity;
        this.discount = 0.0;
    }
    
    public double getFinalPrice() {
        return price * quantity * (1.0 - discount);
    }
}