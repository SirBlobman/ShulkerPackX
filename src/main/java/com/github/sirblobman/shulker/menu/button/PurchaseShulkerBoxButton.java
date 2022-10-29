package com.github.sirblobman.shulker.menu.button;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.adventure.adventure.text.Component;
import com.github.sirblobman.api.adventure.adventure.text.TextReplacementConfig;
import com.github.sirblobman.api.language.ComponentHelper;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.language.SimpleReplacer;
import com.github.sirblobman.api.menu.button.QuickButton;
import com.github.sirblobman.api.nms.ItemHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.ItemUtility;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.api.utility.paper.PaperChecker;
import com.github.sirblobman.api.utility.paper.PaperHelper;
import com.github.sirblobman.api.xseries.XMaterial;
import com.github.sirblobman.shulker.manager.VaultManager;
import com.github.sirblobman.shulker.ShulkerPlugin;
import com.github.sirblobman.shulker.manager.ShopAccessManager;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public final class PurchaseShulkerBoxButton extends QuickButton {
    private final ShulkerPlugin plugin;
    private final XMaterial material;

    public PurchaseShulkerBoxButton(ShulkerPlugin plugin, XMaterial material) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
        this.material = Validate.notNull(material, "material must not be null!");
    }

    public ShulkerPlugin getPlugin() {
        return this.plugin;
    }

    public XMaterial getMaterial() {
        return this.material;
    }

    @Override
    public void onLeftClick(Player player, boolean shift) {
        ShulkerPlugin plugin = getPlugin();
        LanguageManager languageManager = plugin.getLanguageManager();
        ShopAccessManager shopAccessManager = plugin.getShopAccessManager();

        XMaterial material = getMaterial();
        if (shopAccessManager.hasAccess(player, material)) {
            return;
        }

        String materialName = material.name();
        YamlConfiguration configuration = plugin.getConfig();
        double price = configuration.getDouble("shop-menu.price." + materialName, 5.0D);
        if (price <= 0.0D) {
            shopAccessManager.addAccess(player, material);
            sendPurchaseSuccessfulMessage(player, material);
            closeMenu(player);
            return;
        }

        VaultManager hookVault = plugin.getHookVault();
        Economy economy = hookVault.getEconomy();
        double balance = economy.getBalance(player);
        if (balance < price) {
            languageManager.sendMessage(player, "error.not-enough-money", null);
            closeMenu(player);
            return;
        }

        EconomyResponse economyResponse = economy.withdrawPlayer(player, price);
        if (!economyResponse.transactionSuccess()) {
            Replacer replacer = new SimpleReplacer("{error}", economyResponse.errorMessage);
            languageManager.sendMessage(player, "error.economy-error", replacer);
            closeMenu(player);
            return;
        }

        shopAccessManager.addAccess(player, material);
        sendPurchaseSuccessfulMessage(player, material);
        closeMenu(player);
    }

    private void closeMenu(Player player) {
        ShulkerPlugin plugin = getPlugin();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(plugin, player::closeInventory, 1L);
    }

    private void sendPurchaseSuccessfulMessage(Player player, XMaterial material) {
        ShulkerPlugin plugin = getPlugin();
        LanguageManager languageManager = plugin.getLanguageManager();
        Component preMessage = languageManager.getMessage(player, "shop-menu.purchase-success", null);

        Component message = replaceDisplayName(preMessage, material);
        languageManager.sendMessage(player, message);
    }

    private Component replaceDisplayName(Component original, XMaterial material) {
        ItemStack item = material.parseItem();
        Component displayName = getDisplayName(item);

        TextReplacementConfig.Builder builder = TextReplacementConfig.builder();
        builder.matchLiteral("{type}");
        builder.replacement(displayName);

        TextReplacementConfig replacementConfig = builder.build();
        return original.replaceText(replacementConfig);
    }

    private Component getDisplayName(ItemStack item) {
        if (ItemUtility.isAir(item)) {
            return Component.text("null");
        }

        if (PaperChecker.hasNativeComponentSupport()) {
            Component displayName = PaperHelper.getDisplayName(item);
            if (displayName != null) {
                return displayName;
            }
        } else {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null && itemMeta.hasDisplayName()) {
                String displayName = itemMeta.getDisplayName();
                return ComponentHelper.toComponent(displayName);
            }
        }

        ShulkerPlugin plugin = getPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        ItemHandler itemHandler = multiVersionHandler.getItemHandler();
        String localizedName = itemHandler.getLocalizedName(item);
        return Component.text(localizedName);
    }
}
