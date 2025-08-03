package com.github.sirblobman.shulker.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.plugin.listener.PluginListener;
import com.github.sirblobman.api.utility.ItemUtility;
import com.github.sirblobman.shulker.ShulkerPlugin;
import com.github.sirblobman.shulker.manager.ShopAccessManager;
import com.github.sirblobman.shulker.menu.ShulkerBoxMenu;
import com.github.sirblobman.api.shaded.xseries.XMaterial;

public final class ListenerClick extends PluginListener<ShulkerPlugin> {
    public ListenerClick(@NotNull ShulkerPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        if(!isEnabled()) {
            return;
        }

        HumanEntity clicker = e.getWhoClicked();
        if (!(clicker instanceof Player player)) {
            return;
        }

        if(e.getView().getTopInventory().getHolder() instanceof ShulkerBoxMenu) {
            return;
        }

        ClickType clickType = e.getClick();
        if (clickType != ClickType.SHIFT_RIGHT) {
            return;
        }

        ItemStack clickedItem = e.getCurrentItem();
        if (ItemUtility.isAir(clickedItem)) {
            return;
        }

        ItemMeta itemMeta = clickedItem.getItemMeta();
        if (!(itemMeta instanceof BlockStateMeta blockStateMeta)) {
            return;
        }

        BlockState blockState = blockStateMeta.getBlockState();
        if (!(blockState instanceof ShulkerBox shulkerBox)) {
            return;
        }

        if (shulkerBox.getInventory().isEmpty()) {
            return;
        }

        e.setCancelled(true);
        ShulkerPlugin plugin = getPlugin();
        if (plugin.isShopEnabled()) {
            XMaterial material = XMaterial.matchXMaterial(clickedItem);
            ShopAccessManager shopAccessManager = plugin.getShopAccessManager();
            if (!shopAccessManager.hasAccess(player, material)) {
                LanguageManager languageManager = plugin.getLanguageManager();
                languageManager.sendMessage(player, "error.missing-type-access");
                return;
            }
        }

        ShulkerBoxMenu shulkerBoxMenu = new ShulkerBoxMenu(plugin, player, clickedItem);
        shulkerBoxMenu.open();
    }

    private boolean isEnabled() {
        return getPlugin().getMainConfiguration().isAllowInventoryRightClick();
    }
}
