package com.github.sirblobman.shulker;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.update.UpdateManager;
import com.github.sirblobman.shulker.listener.ListenerMenu;

public final class ShulkerPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        new ListenerMenu(this).register();

        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        UpdateManager updateManager = corePlugin.getUpdateManager();
        updateManager.addResource(this, 81793L);
    }
}
