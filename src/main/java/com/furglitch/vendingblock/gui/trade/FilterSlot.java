package com.furglitch.vendingblock.gui.trade;

import com.furglitch.vendingblock.blockentity.VendorBlockEntity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class FilterSlot extends SlotItemHandler {

    private final VendorBlockEntity blockEntity;
    private final int slotIndex;

    public FilterSlot(IItemHandler handler, int index, int x, int y, VendorBlockEntity vendor) {
        super(handler, index, x, y);
        this.blockEntity = vendor;
        this.slotIndex = index;
    }

    @Override
    public void set(ItemStack stack) {

        if (!stack.isEmpty()) {

            ItemStack filter = stack.copy();
            super.set(filter);

            if (slotIndex == 0) {
                blockEntity.setFilterContents(1, filter);
            } else if (slotIndex == 10) {
                blockEntity.setFilterContents(2, filter);
            }
        } else {
            super.set(ItemStack.EMPTY);
            if (slotIndex == 0) {
                blockEntity.setFilterContents(1, ItemStack.EMPTY);
            } else if (slotIndex == 10) {
                blockEntity.setFilterContents(2, ItemStack.EMPTY);
            }
        }

    }
    
    public boolean onClick(ItemStack cursorStack, boolean leftClick) {
        if (leftClick) {
            if (cursorStack.isEmpty()) {
                set(ItemStack.EMPTY);
            } else {
                ItemStack slotStack = cursorStack.copy();
                slotStack.setCount(cursorStack.getCount());
                set(slotStack);
            }
            return true;
        } else {
            if (cursorStack.isEmpty()) {
                set(ItemStack.EMPTY);
            } else {
                ItemStack slotStack = cursorStack.copy();
                slotStack.setCount(getItem().getCount() + 1);
                set(slotStack);
            }
            return true;
        }
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
    }

    @Override
    public ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }
    
}
