package com.SirBlobman.shulker;

import com.SirBlobman.shulker.menu.ShulkerBoxMenu;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ShulkerPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(this, this);
    }
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onRightClick(PlayerInteractEvent e) {
        Action action = e.getAction();
        if(action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
    
        EquipmentSlot hand = e.getHand();
        if(hand != EquipmentSlot.HAND) return;
        
        Result result = e.useItemInHand();
        if(result == Result.DENY) return;
    
        Player player = e.getPlayer();
        if(player.isSneaking()) {
            GameMode gameMode = player.getGameMode();
            if(gameMode == GameMode.CREATIVE) return;
        }
        
        PlayerInventory playerInventory = player.getInventory();
        ItemStack item = playerInventory.getItemInMainHand();
        if(item == null) return;
        
        ItemMeta meta = item.getItemMeta();
        if(!(meta instanceof BlockStateMeta)) return;
        BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
        
        BlockState blockState = blockStateMeta.getBlockState();
        if(!(blockState instanceof ShulkerBox)) return;
        
        e.setUseItemInHand(Result.DENY);
        e.setUseInteractedBlock(Result.DENY);
        ShulkerBoxMenu shulkerBoxMenu = new ShulkerBoxMenu(this, player, item);
        shulkerBoxMenu.open();
    }
}