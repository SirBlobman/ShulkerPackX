package com.github.sirblobman.shulker.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import com.github.sirblobman.api.utility.Validate;

/**
 * This event is fired after a custom shulker box menu is closed and saved.
 * @author SirBlobman
 */
public final class ShulkerBoxPostCloseEvent extends Event {
    private static final HandlerList HANDLER_LIST;
    
    static {
        HANDLER_LIST = new HandlerList();
    }
    
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }
    
    private final ItemStack shulkerBoxItem;
    
    public ShulkerBoxPostCloseEvent(ItemStack shulkerBoxItem) {
        this.shulkerBoxItem = Validate.notNull(shulkerBoxItem, "shulkerBoxItem must not be null!");
    }
    
    public ItemStack getShulkerBoxItem() {
        return this.shulkerBoxItem;
    }
}
