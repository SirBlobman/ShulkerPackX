package com.github.sirblobman.shulker.worldguard;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;

public final class WorldGuardChecker {
    public static boolean canPlace(@NotNull Player player, @NotNull Location bukkitLocation) {
        WorldGuardWrapper worldGuardWrapper = WorldGuardWrapper.getInstance();
        Optional<IWrappedFlag<WrappedState>> optionalBlockPlaceFlag = worldGuardWrapper.getFlag("block-place", WrappedState.class);
        if (optionalBlockPlaceFlag.isEmpty()) {
            // Block Place flag doesn't exist.
            return true;
        }

        IWrappedFlag<WrappedState> blockPlaceFlag = optionalBlockPlaceFlag.get();
        Optional<WrappedState> optionalWrappedState = worldGuardWrapper.queryFlag(player, bukkitLocation, blockPlaceFlag);
        if (optionalWrappedState.isEmpty()) {
            // Block Place flag not set within area.
            return true;
        }

        WrappedState wrappedState = optionalWrappedState.get();
        return (wrappedState == WrappedState.ALLOW);
    }
}
