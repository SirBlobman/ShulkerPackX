package com.github.sirblobman.shulker.event;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * This event is fired after a custom shulker box menu is closed and saved.
 *
 * @author SirBlobman
 */
public final class ShulkerBoxPostCloseEvent extends Event {
    private static final HandlerList HANDLER_LIST;

    static {
        HANDLER_LIST = new HandlerList();
    }

    private final HumanEntity humanEntity;
    private final ItemStack shulkerBoxItem;

    public ShulkerBoxPostCloseEvent(@NotNull HumanEntity human, @NotNull ItemStack item) {
        this.humanEntity = human;
        this.shulkerBoxItem = item;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return getHandlerList();
    }

    public @NotNull HumanEntity getHumanEntity() {
        return this.humanEntity;
    }

    public @NotNull ItemStack getShulkerBoxItem() {
        return this.shulkerBoxItem;
    }
}
