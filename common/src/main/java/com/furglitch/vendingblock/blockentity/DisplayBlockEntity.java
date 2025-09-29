package com.furglitch.vendingblock.blockentity;

import com.furglitch.vendingblock.blockentity.data.OwnerInfo;
import com.furglitch.vendingblock.gui.display.DisplayBlockMenu;
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

public class DisplayBlockEntity extends BaseContainerBlockEntity {

    private static final int inv_size = 1;
    private NonNullList<ItemStack> items;
    private NonNullList<ItemStack> filterItems = NonNullList.withSize(1, ItemStack.EMPTY);
    private ContainerData data;
    private final OwnerInfo ownerInfo = new OwnerInfo();

    public DisplayBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.DISPLAY_BE.get(), pos, state);
        this.items = NonNullList.withSize(inv_size, ItemStack.EMPTY);
        this.data = new ContainerData() {
            @Override public int get(int index) { return items.get(index).getCount(); }
            @Override public void set(int index, int value) { items.get(index).setCount(value); }
            @Override public int getCount() { return inv_size; }
        };
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.vendingblock.display_block");
    }

    @Override public int getContainerSize() { return inv_size; }
    @Override protected NonNullList<ItemStack> getItems() { return this.items; }
    @Override protected void setItems(NonNullList<ItemStack> items) { this.items = items; }
    public ItemStack getFilterItem(int index) {
        if (index < 0 || index >= this.filterItems.size()) return ItemStack.EMPTY;
        return this.filterItems.get(index);
    }
    public void setFilterItem(int index, ItemStack stack) {
        if (index < 0 || index >= this.filterItems.size()) return;
        this.filterItems.set(index, stack);
        this.setChanged();
        if (this.getLevel() != null) {
            this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inv) {
        return new DisplayBlockMenu(id, inv, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        CompoundTag filterTag = new CompoundTag();
        ContainerHelper.saveAllItems(filterTag, this.filterItems, registries);
        tag.put("filter_items", filterTag);
        ownerInfo.saveNBT(tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(inv_size, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
        this.filterItems = NonNullList.withSize(1, ItemStack.EMPTY);
        if (tag.contains("filter_items")) {
            CompoundTag filterTag = tag.getCompound("filter_items");
            ContainerHelper.loadAllItems(filterTag, this.filterItems, registries);
        }
        ownerInfo.loadNBT(tag);
    }

    public OwnerInfo ownerInfo() {
        return ownerInfo;
    }
    
}
