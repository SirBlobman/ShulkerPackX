package com.github.sirblobman.shulker.menu;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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

import com.github.sirblobman.api.adventure.adventure.text.Component;
import com.github.sirblobman.api.language.ComponentHelper;
import com.github.sirblobman.api.menu.AdvancedAbstractMenu;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.ItemUtility;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.api.utility.paper.PaperChecker;
import com.github.sirblobman.api.utility.paper.PaperHelper;
import com.github.sirblobman.shulker.ShulkerPlugin;
import com.github.sirblobman.shulker.event.ShulkerBoxPostCloseEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public MultiVersionHandler getMultiVersionHandler() {
        ShulkerPlugin plugin = getPlugin();
        return plugin.getMultiVersionHandler();
    }

    @Override
    public Component getTitle() {
        Component displayName = getShulkerBoxItemDisplayName();
        if (displayName != null) {
            return displayName;
        }

        return Component.translatable("container.shulkerBox");
    }

    @Override
    public @NotNull Inventory getInventory() {
        Component title = getTitle();
        Inventory inventory = getInventory(27, title);

        ItemStack[] originalContents = getContents();
        inventory.setContents(originalContents.clone());
        return inventory;
    }

    @Override
    public void onValidClose(InventoryCloseEvent e) {
        HumanEntity humanEntity = e.getPlayer();
        ItemStack cursorItem = humanEntity.getItemOnCursor();
        if (!ItemUtility.isAir(cursorItem)) {
            humanEntity.setItemOnCursor(null);
            humanEntity.getWorld().dropItemNaturally(humanEntity.getLocation(), cursorItem);
        }

        Inventory inventory = e.getInventory();
        ItemStack[] contents = inventory.getContents();
        setContents(contents);

        ItemStack item = getShulkerBoxItem();
        ShulkerBoxPostCloseEvent event = new ShulkerBoxPostCloseEvent(humanEntity, item);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void onValidClick(InventoryClickEvent e) {
        printDebug("Detected valid InventoryClickEvent.");

        int slot = e.getRawSlot();
        printDebug("Raw Slot: " + slot);

        if (slot < 0) {
            printDebug("Slot is less than zero, cancelled event.");
            e.setCancelled(true);
        }

        Inventory inventory = e.getInventory();
        InventoryType inventoryType = inventory.getType();
        if (inventoryType == InventoryType.PLAYER) {
            printDebug("inventory type is not player, ignoring.");
            return;
        }

        ItemStack cursorItem = e.getCursor();
        ItemStack currentItem = e.getCurrentItem();

        if (isShulkerBox(cursorItem)) {
            printDebug("Cursor item is shulker box, cancelling.");
            e.setCancelled(true);
        }

        if (isShulkerBox(currentItem)) {
            printDebug("Clicked item is shulker box, cancelling.");
            e.setCancelled(true);
        }

        printDebug("Done checking event.");
    }

    @Override
    public void onValidDrag(InventoryDragEvent e) {
        Set<Integer> rawSlots = e.getRawSlots();
        for (Integer rawSlot : rawSlots) {
            if (rawSlot == null || rawSlot < 0) {
                e.setCancelled(true);
            }
        }

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

    @Nullable
    private Component getShulkerBoxItemDisplayName() {
        ItemStack item = getShulkerBoxItem();
        if (ItemUtility.isAir(item)) {
            return null;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return null;
        }

        if (!itemMeta.hasDisplayName()) {
            return null;
        }

        if (PaperChecker.hasNativeComponentSupport()) {
            return PaperHelper.getDisplayName(item);
        } else {
            String legacyDisplayName = itemMeta.getDisplayName();
            return ComponentHelper.toComponent(legacyDisplayName);
        }
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
        if (itemMeta == null) {
            throw new IllegalStateException("null item meta!");
        }

        BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
        BlockState blockState = blockStateMeta.getBlockState();

        ShulkerBox shulkerBox = (ShulkerBox) blockState;
        Inventory inventory = shulkerBox.getInventory();
        return inventory.getContents();
    }

    private void setContents(ItemStack[] contents) {
        ItemStack item = getShulkerBoxItem();
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            throw new IllegalStateException("null item meta!");
        }

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

    private void printDebug(String message) {
        ShulkerPlugin plugin = getPlugin();
        plugin.printDebug(message);
    }
}
