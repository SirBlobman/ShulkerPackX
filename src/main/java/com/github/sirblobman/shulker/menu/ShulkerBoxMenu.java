package com.github.sirblobman.shulker.menu;

import java.util.Collection;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.sirblobman.api.menu.AdvancedAbstractMenu;
import com.github.sirblobman.api.utility.ItemUtility;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.shulker.ShulkerPlugin;
import com.github.sirblobman.shulker.event.ShulkerBoxPostCloseEvent;

public final class ShulkerBoxMenu extends AdvancedAbstractMenu<ShulkerPlugin> {
    private final ItemStack shulkerBoxItem;

    public ShulkerBoxMenu(ShulkerPlugin plugin, Player player, ItemStack shulkerBoxItem) {
        super(plugin, player);
        this.shulkerBoxItem = Validate.notNull(shulkerBoxItem, "shulkerBoxItem must not be null!");

        if (!isShulkerBox(this.shulkerBoxItem)) {
            throw new IllegalArgumentException("shulkerBoxItem must be a shulker box!");
        }
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = getInventory(27, "Shulker Box");
        ItemStack[] originalContents = getContents();
        inventory.setContents(originalContents.clone());
        return inventory;
    }

    @Override
    public void onValidClose(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();
        ItemStack[] contents = inventory.getContents();
        setContents(contents);

        ItemStack item = getShulkerBoxItem();
        HumanEntity humanEntity = e.getPlayer();
        ShulkerBoxPostCloseEvent event = new ShulkerBoxPostCloseEvent(humanEntity, item);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void onValidClick(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();
        InventoryType inventoryType = inventory.getType();
        if (inventoryType == InventoryType.PLAYER) {
            return;
        }

        e.setCancelled(false);
        ItemStack cursorItem = e.getCursor();
        ItemStack currentItem = e.getCurrentItem();

        if (isShulkerBox(cursorItem)) {
            e.setCancelled(true);
        }

        if (isShulkerBox(currentItem)) {
            e.setCancelled(true);
        }
    }

    @Override
    public void onValidDrag(InventoryDragEvent e) {
        Inventory inventory = e.getInventory();
        InventoryType inventoryType = inventory.getType();
        if (inventoryType == InventoryType.PLAYER) {
            return;
        }

        e.setCancelled(false);
        Map<Integer, ItemStack> newItemMap = e.getNewItems();
        Collection<ItemStack> valueCollection = newItemMap.values();

        for (ItemStack item : valueCollection) {
            if (!isShulkerBox(item)) {
                continue;
            }

            e.setCancelled(true);
            break;
        }
    }

    private ItemStack getShulkerBoxItem() {
        return this.shulkerBoxItem;
    }

    private boolean isShulkerBox(ItemStack item) {
        if (ItemUtility.isAir(item)) {
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (!(itemMeta instanceof BlockStateMeta)) {
            return false;
        }

        BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
        BlockState blockState = blockStateMeta.getBlockState();
        return (blockState instanceof ShulkerBox);
    }

    private ItemStack[] getContents() {
        ItemStack item = getShulkerBoxItem();
        ItemMeta itemMeta = item.getItemMeta();
        BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
        BlockState blockState = blockStateMeta.getBlockState();

        ShulkerBox shulkerBox = (ShulkerBox) blockState;
        Inventory inventory = shulkerBox.getInventory();
        return inventory.getContents();
    }

    private void setContents(ItemStack[] contents) {
        ItemStack item = getShulkerBoxItem();
        ItemMeta itemMeta = item.getItemMeta();
        BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
        BlockState blockState = blockStateMeta.getBlockState();

        ShulkerBox shulkerBox = (ShulkerBox) blockState;
        Inventory inventory = shulkerBox.getInventory();
        inventory.setContents(contents);

        blockStateMeta.setBlockState(shulkerBox);
        item.setItemMeta(blockStateMeta);

        Player player = getPlayer();
        player.updateInventory();
    }
}
