package com.furglitch.vendingblock.blockentity;

import com.furglitch.vendingblock.registry.BlockEntityRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DisplayBlockEntity extends BlockEntity {

    public DisplayBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.DISPLAY_BE.get(), pos, state);
    }
    
}
