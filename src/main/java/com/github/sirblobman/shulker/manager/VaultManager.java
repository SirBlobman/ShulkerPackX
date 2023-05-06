package com.github.sirblobman.shulker.manager;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

import com.github.sirblobman.shulker.ShulkerPlugin;

import net.milkbowl.vault.economy.Economy;

public final class VaultManager {
    private final ShulkerPlugin plugin;
    private Economy economy;

    public VaultManager(@NotNull ShulkerPlugin plugin) {
        this.plugin = plugin;
        this.economy = null;
    }

    public boolean setupEconomy() {
        Logger logger = getLogger();
        ServicesManager servicesManager = Bukkit.getServicesManager();
        RegisteredServiceProvider<Economy> registration = servicesManager.getRegistration(Economy.class);
        if (registration == null) {
            logger.log(Level.WARNING, "Vault is installed, but an economy plugin is not registered.");
            return false;
        }

        this.economy = registration.getProvider();
        String providerName = this.economy.getName();
        Plugin providerPlugin = registration.getPlugin();
        String providerPluginName = providerPlugin.getName();
        String providerPluginVersion = providerPlugin.getDescription().getVersion();

        String logMessage = String.format("Hooked into economy '%s' from plugin '%s v%s'.", providerName,
                providerPluginName, providerPluginVersion);
        logger.info(logMessage);
        return true;
    }

    public @NotNull Economy getEconomy() {
        if (this.economy == null) {
            throw new IllegalStateException("setupEconomy() not run yet.");
        }

        return this.economy;
    }

    private @NotNull Logger getLogger() {
        return this.plugin.getLogger();
    }
}
