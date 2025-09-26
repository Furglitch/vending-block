package com.furglitch.vendingblock.gui.components;

import com.furglitch.vendingblock.blockentity.VendorBlockEntity;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class GhostFilterSlot extends Slot {

    private final VendorBlockEntity vendor;
    private final int index;

    public GhostFilterSlot(VendorBlockEntity vendor, int index, int x, int y) {
        super(new SimpleContainer(1), 0, x, y);
        this.vendor = vendor;
        this.index = index;
    }

    @Override
    public void set(ItemStack stack) {
        ItemStack copy = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        if (!copy.isEmpty()) copy.setCount(1);
        if (vendor != null) vendor.setFilterItem(index, copy);
        super.set(copy);
    }

    @Override
    public ItemStack getItem() {
        ItemStack display = super.getItem();
        if (!display.isEmpty()) return display;
        if (vendor == null) return ItemStack.EMPTY;
        return vendor.getFilterItem(index);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.vendor == null;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        set(ItemStack.EMPTY);
    }

    public boolean onClick(ItemStack cursorStack, boolean leftClick) {
    try {
            if (cursorStack.isEmpty()) {
                set(ItemStack.EMPTY);
            } else {
                ItemStack slotStack = cursorStack.copy();
                slotStack.setCount(1);
                set(slotStack);
            }
            return true;
        } finally {
        }
    }
}
