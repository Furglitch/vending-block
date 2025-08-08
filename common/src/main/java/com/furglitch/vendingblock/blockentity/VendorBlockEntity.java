package com.furglitch.vendingblock.blockentity;

import com.furglitch.vendingblock.blockentity.data.OwnerInfo;
import com.furglitch.vendingblock.registry.BlockEntityRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VendorBlockEntity extends BlockEntity {

    private final OwnerInfo ownerInfo = new OwnerInfo();
    public boolean infiniteStock = false;
    public boolean trashPayments = false;
    public boolean hasError = false;
    public enum ErrorState {
        NONE,
        EMPTY_STOCK,
        FULL_INVENTORY,
        NOT_SET_UP
    }
    public ErrorState error = ErrorState.NONE;

    public VendorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.VENDOR_BE.get(), pos, state);
    }

    public OwnerInfo ownerInfo() {
        return ownerInfo;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        ownerInfo.saveNBT(tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        ownerInfo.loadNBT(tag);
    }
    
}
