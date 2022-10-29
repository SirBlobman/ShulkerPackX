package com.github.sirblobman.shulker.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.command.PlayerCommand;
import com.github.sirblobman.shulker.ShulkerPlugin;
import com.github.sirblobman.shulker.menu.ShulkerShopMenu;

public final class CommandShulkerPackShop extends PlayerCommand {
    private final ShulkerPlugin plugin;

    public CommandShulkerPackShop(ShulkerPlugin plugin) {
        super(plugin, "shulker-shop");
        this.plugin = plugin;
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] strings) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(Player player, String[] strings) {
        if (!this.plugin.isShopEnabled()) {
            sendMessage(player, "error.shop-disabled", null);
            return true;
        }

        new ShulkerShopMenu(this.plugin, player).open();
        return true;
    }
}
