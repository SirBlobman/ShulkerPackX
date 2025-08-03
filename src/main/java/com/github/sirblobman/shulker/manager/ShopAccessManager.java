package com.github.sirblobman.shulker.manager;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.github.sirblobman.shulker.ShulkerPlugin;
import com.github.sirblobman.api.shaded.xseries.XMaterial;

public final class ShopAccessManager {
    private final ShulkerPlugin plugin;

    public ShopAccessManager(@NotNull ShulkerPlugin plugin) {
        this.plugin = plugin;
    }

    private @NotNull ShulkerPlugin getPlugin() {
        return this.plugin;
    }

    private @NotNull NamespacedKey getShopAccessKey() {
        ShulkerPlugin plugin = getPlugin();
        return new NamespacedKey(plugin, "shop-access-values");
    }

    private @NotNull PersistentDataContainer getShopAccessContainer(@NotNull Player player) {
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

    @SuppressWarnings("deprecation") // getKey() is deprecated in Spigot, but it's the only method on Paper
    public boolean hasAccess(@NotNull Player player, @NotNull XMaterial shulkerPackType) {
        Material bukkitMaterial = shulkerPackType.get();
        if (bukkitMaterial == null) {
            throw new IllegalArgumentException("Your Spigot version is missing the '" + shulkerPackType
                    + "' material value.");
        }

        // getKey() is deprecated in Spigot, but it's the only method on Paper
        NamespacedKey materialKey = bukkitMaterial.getKey();

        PersistentDataContainer shopAccessContainer = getShopAccessContainer(player);
        byte access = shopAccessContainer.getOrDefault(materialKey, PersistentDataType.BYTE, (byte) 0);
        return (access == 1);
    }

    @SuppressWarnings("deprecation")
    public void addAccess(@NotNull Player player, @NotNull XMaterial shulkerPackType) {
        Material bukkitMaterial = shulkerPackType.get();
        if (bukkitMaterial == null) {
            throw new IllegalArgumentException("Your Spigot version is missing the '" + shulkerPackType
                    + "' material value.");
        }

        // getKey() is deprecated in Spigot, but it's the only method on Paper
        NamespacedKey materialKey = bukkitMaterial.getKey();

        PersistentDataContainer shopAccessContainer = getShopAccessContainer(player);
        shopAccessContainer.set(materialKey, PersistentDataType.BYTE, (byte) 1);

        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        dataContainer.set(getShopAccessKey(), PersistentDataType.TAG_CONTAINER, shopAccessContainer);
        getPlugin().printDebug("Set material key '" + materialKey + "' to '1' for player '" + player + "'.");
    }
}
