package com.github.sirblobman.shulker.configuration;

import java.util.Locale;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;
import com.github.sirblobman.shulker.ShulkerPlugin;
import com.github.sirblobman.api.shaded.adventure.key.Key;
import com.github.sirblobman.api.shaded.adventure.sound.Sound;

import org.intellij.lang.annotations.Subst;

public final class MainConfiguration implements IConfigurable {
    private final ShulkerPlugin plugin;
    private final ShopMenuConfiguration shopMenuConfiguration;

    private boolean debugMode;
    private boolean allowInventoryRightClick;
    private boolean worldGuardMode;

    private Sound openSound;
    private Sound closeSound;

    public MainConfiguration(@NotNull ShulkerPlugin plugin) {
        this.plugin = plugin;
        this.shopMenuConfiguration = new ShopMenuConfiguration();

        this.debugMode = false;
        this.allowInventoryRightClick = false;
        this.worldGuardMode = false;
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        setDebugMode(config.getBoolean("debug-mode", false));
        setAllowInventoryRightClick(config.getBoolean("allow-inventory-right-click", false));
        setWorldGuardMode(config.getBoolean("world-guard-mode", false));

        ConfigurationSection shopMenuSection = getOrCreateSection(config, "shop-menu");
        this.shopMenuConfiguration.load(shopMenuSection);

        setOpenSound(parseSound(getOrCreateSection(config, "open-sound")));
        setCloseSound(parseSound(getOrCreateSection(config, "close-sound")));
    }

    private @Nullable Sound parseSound(@NotNull ConfigurationSection section) {
        @Subst("ignore")
        String soundKeyString = section.getString("sound");
        if (soundKeyString == null || soundKeyString.isBlank()) {
            return null;
        }

        String categoryName = section.getString("category", "master");
        Sound.Source category = Sound.Source.valueOf(categoryName.toUpperCase(Locale.US));

        Key soundKey = Key.key(soundKeyString);
        float volume = (float) section.getDouble("volume", 1.0D);
        float pitch = (float) section.getDouble("pitch", 1.0D);

        Sound.Builder builder = Sound.sound();
        builder.type(soundKey);
        builder.source(category);
        builder.volume(volume);
        builder.pitch(pitch);
        return builder.build();
    }

    private @NotNull ShulkerPlugin getPlugin() {
        return this.plugin;
    }

    private @NotNull Logger getLogger() {
        return getPlugin().getLogger();
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

    public boolean isWorldGuardMode() {
        return this.worldGuardMode;
    }

    public void setWorldGuardMode(boolean worldGuardMode) {
        this.worldGuardMode = worldGuardMode;
    }

    public @Nullable Sound getOpenSound() {
        return openSound;
    }

    public void setOpenSound(@Nullable Sound openSound) {
        this.openSound = openSound;
    }

    public @Nullable Sound getCloseSound() {
        return closeSound;
    }

    public void setCloseSound(@Nullable Sound closeSound) {
        this.closeSound = closeSound;
    }
}
