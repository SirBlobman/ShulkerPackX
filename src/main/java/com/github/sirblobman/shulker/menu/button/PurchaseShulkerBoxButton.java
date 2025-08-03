package com.github.sirblobman.shulker.menu.button;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.sirblobman.api.folia.FoliaHelper;
import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.api.nms.ItemHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.ItemUtility;
import com.github.sirblobman.api.utility.paper.PaperChecker;
import com.github.sirblobman.api.utility.paper.PaperHelper;
import com.github.sirblobman.shulker.ShulkerPlugin;
import com.github.sirblobman.shulker.configuration.ShopMenuConfiguration;
import com.github.sirblobman.shulker.manager.ShopAccessManager;
import com.github.sirblobman.shulker.manager.VaultManager;
import com.github.sirblobman.shulker.menu.task.CloseMenuTask;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.api.shaded.adventure.text.TextReplacementConfig;
import com.github.sirblobman.api.shaded.xseries.XMaterial;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public final class PurchaseShulkerBoxButton extends QuickButton {
    private final ShulkerPlugin plugin;
    private final XMaterial material;

    public PurchaseShulkerBoxButton(@NotNull ShulkerPlugin plugin, @NotNull XMaterial material) {
        this.plugin = plugin;
        this.material = material;
    }

    public @NotNull ShulkerPlugin getPlugin() {
        return this.plugin;
    }

    public @NotNull XMaterial getMaterial() {
        return this.material;
    }

    @Override
    public void onLeftClick(@NotNull Player player, boolean shift) {
        ShulkerPlugin plugin = getPlugin();
        LanguageManager languageManager = plugin.getLanguageManager();
        ShopAccessManager shopAccessManager = plugin.getShopAccessManager();

        XMaterial material = getMaterial();
        if (shopAccessManager.hasAccess(player, material)) {
            return;
        }

        ShopMenuConfiguration configuration = plugin.getMainConfiguration().getShopMenuConfiguration();
        double price = configuration.getPrice(material);
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
            languageManager.sendMessage(player, "error.not-enough-money");
            closeMenu(player);
            return;
        }

        EconomyResponse economyResponse = economy.withdrawPlayer(player, price);
        if (!economyResponse.transactionSuccess()) {
            Replacer replacer = new StringReplacer("{error}", economyResponse.errorMessage);
            languageManager.sendMessage(player, "error.economy-error", replacer);
            closeMenu(player);
            return;
        }

        shopAccessManager.addAccess(player, material);
        sendPurchaseSuccessfulMessage(player, material);
        closeMenu(player);
    }

    private void closeMenu(@NotNull Player player) {
        ShulkerPlugin plugin = getPlugin();
        CloseMenuTask task = new CloseMenuTask(plugin, player);

        FoliaHelper foliaHelper = plugin.getFoliaHelper();
        TaskScheduler scheduler = foliaHelper.getScheduler();
        scheduler.scheduleEntityTask(task);
    }

    private void sendPurchaseSuccessfulMessage(@NotNull Player player, @NotNull XMaterial material) {
        ShulkerPlugin plugin = getPlugin();
        LanguageManager languageManager = plugin.getLanguageManager();
        Component preMessage = languageManager.getMessage(player, "shop-menu.purchase-success");

        Component message = replaceDisplayName(preMessage, material);
        languageManager.sendMessage(player, message);
    }

    private @NotNull Component replaceDisplayName(@NotNull Component original, @NotNull XMaterial material) {
        ItemStack item = material.parseItem();
        Component displayName = getDisplayName(item);

        TextReplacementConfig.Builder builder = TextReplacementConfig.builder();
        builder.matchLiteral("{type}");
        builder.replacement(displayName);

        TextReplacementConfig replacementConfig = builder.build();
        return original.replaceText(replacementConfig);
    }

    private @NotNull Component getDisplayName(@Nullable ItemStack item) {
        if (ItemUtility.isAir(item)) {
            return Component.text("null");
        }

        if (PaperChecker.hasNativeComponentSupport()) {
            Component displayName = PaperHelper.getDisplayName(item);
            if (displayName != null) {
                return displayName;
            }
        }

        ShulkerPlugin plugin = getPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        ItemHandler itemHandler = multiVersionHandler.getItemHandler();
        Component displayName = itemHandler.getDisplayName(item);
        if (displayName != null) {
            return displayName;
        }

        String localizedName = itemHandler.getLocalizedName(item);
        return Component.text(localizedName);
    }
}
