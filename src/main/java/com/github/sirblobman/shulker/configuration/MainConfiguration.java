package com.github.sirblobman.shulker.configuration;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class MainConfiguration implements IConfigurable {
    private boolean debugMode;
    private boolean allowInventoryRightClick;
    private final ShopMenuConfiguration shopMenuConfiguration;

    public MainConfiguration() {
        this.debugMode = false;
        this.allowInventoryRightClick = false;
        this.shopMenuConfiguration = new ShopMenuConfiguration();
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        setDebugMode(config.getBoolean("debug-mode", false));
        setAllowInventoryRightClick(config.getBoolean("allow-inventory-right-click", false));

        ConfigurationSection shopMenuSection = getOrCreateSection(config, "shop-menu");
        this.shopMenuConfiguration.load(shopMenuSection);
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public boolean isAllowInventoryRightClick() {
        return this.allowInventoryRightClick;
    }

    public void setAllowInventoryRightClick(boolean allowInventoryRightClick) {
        this.allowInventoryRightClick = allowInventoryRightClick;
    }

    public @NotNull ShopMenuConfiguration getShopMenuConfiguration() {
        return this.shopMenuConfiguration;
    }
}
