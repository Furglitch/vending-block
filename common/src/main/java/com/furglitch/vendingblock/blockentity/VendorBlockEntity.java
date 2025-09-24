package com.furglitch.vendingblock.blockentity;

import com.furglitch.vendingblock.blockentity.data.OwnerInfo;
import com.furglitch.vendingblock.gui.trade.VendorBlockMenu;
import com.furglitch.vendingblock.registry.BlockEntityRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VendorBlockEntity extends BaseContainerBlockEntity {

    private static final int inv_size = 9;
    private NonNullList<ItemStack> items;
    private ContainerData data;

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
        this.items = NonNullList.withSize(inv_size, ItemStack.EMPTY);
        this.data = new ContainerData() {
            @Override public int get(int index) { return items.get(index).getCount(); }
            @Override public void set(int index, int value) { items.get(index).setCount(value); }
            @Override public int getCount() { return inv_size; }
        };
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.vendingblock.vending_block");
    }

    @Override protected NonNullList<ItemStack> getItems() { return this.items; }
    @Override protected void setItems(NonNullList<ItemStack> items) { this.items = items; }

    @Override public int getContainerSize() { return inv_size; }
    
    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inv) {
        return new VendorBlockMenu(id, inv, this, this.data);
    }

    public OwnerInfo ownerInfo() {
        return ownerInfo;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
    ContainerHelper.saveAllItems(tag, this.items, registries);
        ownerInfo.saveNBT(tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(inv_size, ItemStack.EMPTY);
    ContainerHelper.loadAllItems(tag, this.items, registries);
        ownerInfo.loadNBT(tag);
    }
    
}
