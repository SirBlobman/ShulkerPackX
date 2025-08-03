package com.github.sirblobman.shulker.menu;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import com.github.sirblobman.api.language.ComponentHelper;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.api.utility.paper.PaperChecker;
import com.github.sirblobman.api.utility.paper.PaperHelper;
import com.github.sirblobman.api.shaded.adventure.text.Component;

public abstract class BaseMenu implements IMenu {
    private IMenu parentMenu;

    public BaseMenu(@Nullable IMenu parentMenu) {
        this.parentMenu = parentMenu;
    }

    @Override
    public @NotNull Optional<IMenu> getParentMenu() {
        return Optional.ofNullable(this.parentMenu);
    }

    @Override
    public void setParentMenu(@NotNull IMenu parentMenu) {
        this.parentMenu = parentMenu;
    }

    /**
     * @param size The size of the inventory. Must be five for a hopper menu or a non-zero multiple of
     *             nine for a chest menu.
     * @return An empty {@link Inventory} instance with this menu instance as its holder.
     */
    public @NotNull Inventory getInventory(int size) {
        if (size == 5) {
            return Bukkit.createInventory(this, InventoryType.HOPPER);
        }

        if (size < 9) {
            throw new IllegalArgumentException("size must be equal to 5 or at least 9");
        }

        if (size > 54) {
            throw new IllegalArgumentException("size cannot be more than 54");
        }

        if (size % 9 != 0) {
            throw new IllegalArgumentException("size must be equal to 5 or divisible by 9");
        }

        return Bukkit.createInventory(this, size);
    }

    /**
     * @param size  The size of the inventory. Must be five for a hopper menu or a non-zero multiple of
     *              nine for a chest menu.
     * @param title The title of the GUI. Legacy color codes that use the &amp; symbol will be automatically translated.
     * @return An empty {@link Inventory} instance with this menu instance as its holder.
     */
    public @NotNull Inventory getInventory(int size, @Nullable String title) {
        if (title == null) {
            return getInventory(size);
        }

        String colorTitle = MessageUtility.color(title);

        if (size == 5) {
            return Bukkit.createInventory(this, InventoryType.HOPPER, colorTitle);
        }

        if (size < 9) {
            throw new IllegalArgumentException("size must be equal to 5 or at least 9");
        }

        if (size > 54) {
            throw new IllegalArgumentException("size cannot be more than 54");
        }

        if (size % 9 != 0) {
            throw new IllegalArgumentException("size must be equal to 5 or divisible by 9");
        }

        return Bukkit.createInventory(this, size, colorTitle);
    }

    /**
     * @param size  The size of the inventory. Must be five for a hopper menu or a non-zero multiple of
     *              nine for a chest menu.
     * @param title The component title for the GUI.
     * @return An empty {@link Inventory} instance with this menu instance as its holder.
     */
    public @NotNull Inventory getInventory(int size, @Nullable Component title) {
        if (title == null) {
            return getInventory(size);
        }

        if (PaperChecker.hasNativeComponentSupport()) {
            return PaperHelper.createInventory(this, size, title);
        } else {
            String legacyTitle = ComponentHelper.toLegacy(title);
            return getInventory(size, legacyTitle);
        }
    }

}
