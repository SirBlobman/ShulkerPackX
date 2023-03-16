package com.github.sirblobman.shulker.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.sirblobman.api.adventure.adventure.text.Component;
import com.github.sirblobman.api.item.ItemBuilder;
import com.github.sirblobman.api.language.ComponentHelper;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.api.menu.AbstractMenu;
import com.github.sirblobman.api.menu.button.AbstractButton;
import com.github.sirblobman.api.menu.button.ExitButton;
import com.github.sirblobman.api.nms.ItemHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.xseries.XMaterial;
import com.github.sirblobman.shulker.ShulkerPlugin;
import com.github.sirblobman.shulker.manager.ShopAccessManager;
import com.github.sirblobman.shulker.manager.VaultManager;
import com.github.sirblobman.shulker.menu.button.PurchaseShulkerBoxButton;

import net.milkbowl.vault.economy.Economy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ShulkerShopMenu extends AbstractMenu {
    private static final List<XMaterial> SHULKER_BOX_MATERIALS;

    static {
        SHULKER_BOX_MATERIALS = Arrays.asList(XMaterial.SHULKER_BOX, XMaterial.WHITE_SHULKER_BOX,
                XMaterial.ORANGE_SHULKER_BOX, XMaterial.MAGENTA_SHULKER_BOX, XMaterial.LIGHT_BLUE_SHULKER_BOX,
                XMaterial.YELLOW_SHULKER_BOX, XMaterial.LIME_SHULKER_BOX, XMaterial.PINK_SHULKER_BOX,
                XMaterial.GRAY_SHULKER_BOX, XMaterial.LIGHT_GRAY_SHULKER_BOX, XMaterial.CYAN_SHULKER_BOX,
                XMaterial.PURPLE_SHULKER_BOX, XMaterial.BLUE_SHULKER_BOX, XMaterial.BROWN_SHULKER_BOX,
                XMaterial.GREEN_SHULKER_BOX, XMaterial.RED_SHULKER_BOX, XMaterial.BLACK_SHULKER_BOX);
    }

    private final ShulkerPlugin plugin;

    public ShulkerShopMenu(ShulkerPlugin plugin, Player player) {
        super(plugin, player);
        this.plugin = plugin;
    }

    private ShulkerPlugin getShulkerPlugin() {
        return this.plugin;
    }

    @Override
    public @NotNull MultiVersionHandler getMultiVersionHandler() {
        ShulkerPlugin plugin = getShulkerPlugin();
        return plugin.getMultiVersionHandler();
    }

    @Override
    public @NotNull LanguageManager getLanguageManager() {
        ShulkerPlugin plugin = getShulkerPlugin();
        return plugin.getLanguageManager();
    }

    @Override
    public int getSize() {
        return 18;
    }

    @Override
    public @Nullable ItemStack getItem(int slot) {
        if (slot >= 0 && slot < 17) {
            return getShulkerBoxItem(slot);
        }

        if (slot == 17) {
            return getExitItem();
        }

        return null;
    }

    @Override
    public @Nullable AbstractButton getButton(int slot) {
        if (slot >= 0 && slot < 17) {
            ShulkerPlugin plugin = getShulkerPlugin();
            XMaterial material = SHULKER_BOX_MATERIALS.get(slot);
            return new PurchaseShulkerBoxButton(plugin, material);
        }

        if (slot == 17) {
            return new ExitButton(this);
        }

        return null;
    }

    @Override
    public @NotNull Component getTitle() {
        Player player = getPlayer();
        LanguageManager languageManager = getLanguageManager();
        return languageManager.getMessage(player, "shop-menu.title");
    }

    @Override
    public boolean shouldPreventClick(int slot) {
        return true;
    }

    private ItemStack getShulkerBoxItem(int slot) {
        XMaterial material = SHULKER_BOX_MATERIALS.get(slot);
        ItemBuilder builder = new ItemBuilder(material);

        ShulkerPlugin plugin = getShulkerPlugin();
        MultiVersionHandler multiVersionHandler = getMultiVersionHandler();
        ItemHandler itemHandler = multiVersionHandler.getItemHandler();

        YamlConfiguration configuration = plugin.getConfig();
        LanguageManager languageManager = getLanguageManager();
        ShopAccessManager shopAccessManager = plugin.getShopAccessManager();

        Player player = getPlayer();
        if (shopAccessManager.hasAccess(player, material)) {
            Component loreLine = languageManager.getMessage(player, "shop-menu.lore.owned");
            builder.withLore(itemHandler, ComponentHelper.wrapNoItalics(loreLine));
            return builder.build();
        }

        String materialName = material.name();
        double price = configuration.getDouble("shop-menu.price." + materialName, 5.0D);

        VaultManager hookVault = plugin.getHookVault();
        Economy economyHandler = hookVault.getEconomy();
        String priceFormatted = economyHandler.format(price);
        Replacer priceReplacer = new StringReplacer("{price}", priceFormatted);

        Component line1 = languageManager.getMessage(player, "shop-menu.lore.not-owned");
        Component line2 = languageManager.getMessage(player, "shop-menu.lore.price", priceReplacer);
        List<Component> lore = Arrays.asList(line1, line2);

        builder.withLore(itemHandler, ComponentHelper.wrapNoItalics(lore));
        return builder.build();
    }

    private ItemStack getExitItem() {
        MultiVersionHandler multiVersionHandler = getMultiVersionHandler();
        ItemHandler itemHandler = multiVersionHandler.getItemHandler();

        Player player = getPlayer();
        LanguageManager languageManager = getLanguageManager();
        Component displayName = languageManager.getMessage(player, "shop-menu.exit-button");

        ItemBuilder builder = new ItemBuilder(XMaterial.BARRIER);
        builder.withName(itemHandler, ComponentHelper.wrapNoItalics(displayName));
        return builder.build();
    }
}
