package com.github.sirblobman.shulker.menu.task;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.folia.details.EntityTaskDetails;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.shulker.ShulkerPlugin;
import com.github.sirblobman.api.shaded.adventure.sound.Sound;

public final class PlaySoundTask extends EntityTaskDetails<Player> {
    private final ShulkerPlugin plugin;
    private final Sound sound;

    public PlaySoundTask(@NotNull ShulkerPlugin plugin, @NotNull Player entity, @NotNull Sound sound) {
        super(plugin, entity);
        this.plugin = plugin;
        this.sound = sound;
    }

    @Override
    public void run() {
        Player player = getEntity();
        if (player == null) {
            return;
        }

        ShulkerPlugin plugin = getShulkerPlugin();
        LanguageManager languageManager = plugin.getLanguageManager();

        Sound sound = getSound();
        languageManager.sendSound(player, sound);
    }

    public @NotNull ShulkerPlugin getShulkerPlugin() {
        return this.plugin;
    }

    public @NotNull Sound getSound() {
        return this.sound;
    }
}
