package com.github.sirblobman.shulker.command;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.command.PlayerCommand;
import com.github.sirblobman.shulker.ShulkerPlugin;
import com.github.sirblobman.shulker.menu.ShulkerShopMenu;

public final class CommandShulkerPackShop extends PlayerCommand {
    private final ShulkerPlugin plugin;

    public CommandShulkerPackShop(@NotNull ShulkerPlugin plugin) {
        super(plugin, "shulker-shop");
        this.plugin = plugin;
    }

    @Override
    protected @NotNull List<String> onTabComplete(@NotNull Player player, String @NotNull [] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NotNull Player player, String @NotNull [] args) {
        ShulkerPlugin plugin = getShulkerPlugin();
        if (!plugin.isShopEnabled()) {
            sendMessage(player, "error.shop-disabled");
            return true;
        }

        new ShulkerShopMenu(plugin, player).open();
        return true;
    }

    private @NotNull ShulkerPlugin getShulkerPlugin() {
        return this.plugin;
    }
}
