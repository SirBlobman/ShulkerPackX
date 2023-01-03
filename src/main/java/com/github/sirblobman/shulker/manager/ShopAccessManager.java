package com.github.sirblobman.shulker.manager;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.api.xseries.XMaterial;
import com.github.sirblobman.shulker.ShulkerPlugin;

import org.jetbrains.annotations.NotNull;

public final class ShopAccessManager {
    private final ShulkerPlugin plugin;

    public ShopAccessManager(ShulkerPlugin plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
    }

    private NamespacedKey getShopAccessKey() {
        return new NamespacedKey(this.plugin, "shop-access-values");
    }

    @NotNull
    private PersistentDataContainer getShopAccessContainer(Player player) {
        Validate.notNull(player, "player must not be null!");
        NamespacedKey shopAccessKey = getShopAccessKey();

        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        if (!dataContainer.has(shopAccessKey, PersistentDataType.TAG_CONTAINER)) {
            PersistentDataAdapterContext adapterContext = dataContainer.getAdapterContext();
            PersistentDataContainer container = adapterContext.newPersistentDataContainer();
            dataContainer.set(shopAccessKey, PersistentDataType.TAG_CONTAINER, container);
            return container;
        }

        PersistentDataContainer container = dataContainer.get(shopAccessKey, PersistentDataType.TAG_CONTAINER);
        if (container == null) {
            throw new NullPointerException("player data container is glitched!");
        }

        return container;
    }

    public boolean hasAccess(Player player, XMaterial shulkerPackType) {
        Validate.notNull(player, "player must not be null!");
        Validate.notNull(shulkerPackType, "shulkerPackType must not be null!");

        Material bukkitMaterial = shulkerPackType.parseMaterial();
        if (bukkitMaterial == null) {
            throw new IllegalArgumentException("Your Spigot version is missing the '" + shulkerPackType
                    + "' material value.");
        }

        NamespacedKey materialKey = bukkitMaterial.getKey();
        PersistentDataContainer shopAccessContainer = getShopAccessContainer(player);
        byte access = shopAccessContainer.getOrDefault(materialKey, PersistentDataType.BYTE, (byte) 0);
        return (access == 1);
    }

    public void addAccess(Player player, XMaterial shulkerPackType) {
        Validate.notNull(player, "player must not be null!");
        Validate.notNull(shulkerPackType, "shulkerPackType must not be null!");

        Material bukkitMaterial = shulkerPackType.parseMaterial();
        if (bukkitMaterial == null) {
            throw new IllegalArgumentException("Your Spigot version is missing the '" + shulkerPackType
                    + "' material value.");
        }

        NamespacedKey materialKey = bukkitMaterial.getKey();
        PersistentDataContainer shopAccessContainer = getShopAccessContainer(player);
        shopAccessContainer.set(materialKey, PersistentDataType.BYTE, (byte) 1);

        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        dataContainer.set(getShopAccessKey(), PersistentDataType.TAG_CONTAINER, shopAccessContainer);
        plugin.printDebug("Set material key '" + materialKey + "' to '1' for player '" + player + "'.");
    }
}
