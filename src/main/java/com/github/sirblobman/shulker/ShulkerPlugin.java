package com.github.sirblobman.shulker;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.bstats.bukkit.Metrics;
import com.github.sirblobman.api.bstats.charts.SimplePie;
import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.language.Language;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.api.update.UpdateManager;
import com.github.sirblobman.shulker.manager.VaultManager;
import com.github.sirblobman.shulker.listener.ListenerMenu;
import com.github.sirblobman.shulker.manager.ShopAccessManager;

import org.jetbrains.annotations.NotNull;

public final class ShulkerPlugin extends ConfigurablePlugin {
    private final ShopAccessManager shopAccessManager;
    private VaultManager hookVault;

    public ShulkerPlugin() {
        this.shopAccessManager = new ShopAccessManager(this);
        this.hookVault = null;
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = this.getConfigurationManager();
        configurationManager.saveDefault("config.yml");

        LanguageManager languageManager = getLanguageManager();
        languageManager.saveDefaultLanguageFiles();
    }

    @Override
    public void onEnable() {
        reloadConfig();
        registerListeners();
        registerUpdateChecker();
        registerbStats();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @Override
    protected void reloadConfiguration() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");

        LanguageManager languageManager = getLanguageManager();
        languageManager.reloadLanguageFiles();

        if (isShopEnabled() && !setupVault()) {
            Logger logger = getLogger();
            logger.warning("The shop is enabled in the configuration, but the Vault economy setup has failed.");
            logger.warning("The shop has been automatically disabled.");
            disableShop();
        }
    }

    @NotNull
    public ShopAccessManager getShopAccessManager() {
        return this.shopAccessManager;
    }

    @NotNull
    public VaultManager getHookVault() {
        if (this.hookVault == null) {
            throw new IllegalStateException("Vault features are not currently enabled!");
        }

        return this.hookVault;
    }

    public boolean isShopEnabled() {
        YamlConfiguration configuration = getConfig();
        return configuration.getBoolean("shop-menu.enabled", true);
    }

    private void disableShop() {
        YamlConfiguration configuration = getConfig();
        configuration.set("shop-menu.enabled", false);
    }

    private boolean setupVault() {
        this.hookVault = null;

        Logger logger = getLogger();
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (!pluginManager.isPluginEnabled("Vault")) {
            logger.warning("Vault is not installed.");
            return false;
        }

        this.hookVault = new VaultManager(this);
        return this.hookVault.setupEconomy();
    }

    private void registerListeners() {
        new ListenerMenu(this).register();
    }

    private void registerUpdateChecker() {
        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        UpdateManager updateManager = corePlugin.getUpdateManager();
        updateManager.addResource(this, 81793L);
    }

    private void registerbStats() {
        Metrics metrics = new Metrics(this, 16178);
        SimplePie languagePie = new SimplePie("selected_language", this::getDefaultLanguageCode);
        metrics.addCustomChart(languagePie);
    }

    private String getDefaultLanguageCode() {
        LanguageManager languageManager = getLanguageManager();
        Language defaultLanguage = languageManager.getDefaultLanguage();
        if (defaultLanguage == null) {
            return "none";
        }

        return defaultLanguage.getLanguageCode();
    }
}
