package com.github.sirblobman.shulker;

import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.language.Language;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.api.update.SpigotUpdateManager;
import com.github.sirblobman.shulker.command.CommandShulkerPackShop;
import com.github.sirblobman.shulker.listener.ListenerMenu;
import com.github.sirblobman.shulker.manager.ShopAccessManager;
import com.github.sirblobman.shulker.manager.VaultManager;
import com.github.sirblobman.api.shaded.bstats.bukkit.Metrics;
import com.github.sirblobman.api.shaded.bstats.charts.SimplePie;

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

        LanguageManager languageManager = getLanguageManager();
        languageManager.onPluginEnable();

        registerCommands();
        registerListeners();
        registerUpdateChecker();
        register_bStats();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    protected void reloadConfiguration() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");

        LanguageManager languageManager = getLanguageManager();
        languageManager.reloadLanguages();

        if (isShopEnabled() && !setupVault()) {
            Logger logger = getLogger();
            logger.warning("The shop is enabled in the configuration, but the Vault economy setup has failed.");
            logger.warning("The shop has been automatically disabled.");
            disableShop();
        }
    }

    public @NotNull ShopAccessManager getShopAccessManager() {
        return this.shopAccessManager;
    }

    public @NotNull VaultManager getHookVault() {
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

    private void registerCommands() {
        new CommandShulkerPackShop(this).register();
    }

    private void registerListeners() {
        new ListenerMenu(this).register();
    }

    private void registerUpdateChecker() {
        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        SpigotUpdateManager updateManager = corePlugin.getSpigotUpdateManager();
        updateManager.addResource(this, 81793L);
    }

    private void register_bStats() {
        Metrics metrics = new Metrics(this, 16178);
        SimplePie languagePie = new SimplePie("selected_language", this::getDefaultLanguageCode);
        metrics.addCustomChart(languagePie);
    }

    private @NotNull String getDefaultLanguageCode() {
        LanguageManager languageManager = getLanguageManager();
        Language defaultLanguage = languageManager.getDefaultLanguage();
        if (defaultLanguage == null) {
            return "none";
        }

        return defaultLanguage.getLanguageName();
    }
}
