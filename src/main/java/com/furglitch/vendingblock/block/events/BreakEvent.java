package com.furglitch.vendingblock.block.events;

import com.furglitch.vendingblock.blockentity.DisplayBlockEntity;
import com.furglitch.vendingblock.blockentity.VendorBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class BreakEvent {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof VendorBlockEntity vendorBlockEntity) {
            if (!vendorBlockEntity.isOwner(player)) {
                event.setCanceled(true);
            }
        }
        else if (be instanceof DisplayBlockEntity displayBlockEntity) {
            if (!displayBlockEntity.isOwner(player)) {
                event.setCanceled(true);
            }
        }

    }
}