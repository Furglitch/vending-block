package com.furglitch.vendingblock.blockentity.transaction;

import com.furglitch.vendingblock.blockentity.VendorBlockEntity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class VendorBlockInventory {

    public static boolean checkInventory(Player buyer, ItemStack price) {
        int inv = 0;
        for ( int i = 0; i < buyer.getInventory().getContainerSize(); i++) {
            ItemStack slot = buyer.getInventory().getItem(i);
            if (slot.getItem().equals(price.getItem())) {
                inv += slot.getCount();
            }
        }
        return inv >= price.getCount();
    }
    
    public static boolean checkInventorySpace(Player buyer, ItemStack price, ItemStack product) {
        ItemStack[] fakeInv = new ItemStack[36];
        for (int i = 0; i < 36; i++) {
            fakeInv[i] = buyer.getInventory().getItem(i).copy();
        }

        int priceCount = price.getCount();
        for (int i = 0; i < 36 && priceCount > 0; i++) {
            ItemStack slot = fakeInv[i];
            if (slot.isEmpty()) {
                int canFit = Math.min(priceCount, price.getMaxStackSize());
                priceCount -= canFit;
            } else if (ItemStack.isSameItemSameComponents(slot, price)) {
                int availableSpace = slot.getMaxStackSize() - slot.getCount();
                int canFit = Math.min(priceCount, availableSpace);
                priceCount -= canFit;
            }
        }

        int remaining = product.getCount();
        for (int i = 0; i < 36 && remaining > 0; i++) {
            ItemStack slot = fakeInv[i];
            if (slot.isEmpty()) {
                int canFit = Math.min(remaining, product.getMaxStackSize());
                remaining -= canFit;
            } else if (ItemStack.isSameItemSameComponents(slot, product)) {
                int availableSpace = slot.getMaxStackSize() - slot.getCount();
                int canFit = Math.min(remaining, availableSpace);
                remaining -= canFit;
            }
        }

        fakeInv = null;
        return remaining <= 0;
    }

    public static boolean checkStock(VendorBlockEntity vendor, ItemStack product) {
        if (vendor.isInfinite()) return true;

        int stock = 0;
        for (int i = 1; i <= 9; i++) {
            ItemStack slot = vendor.inventory.getStackInSlot(i);
            if (slot.getItem().equals(product.getItem())) {
                stock += slot.getCount();
            }
        }

        return stock >= product.getCount();
    }

    public static boolean checkStockSpace(VendorBlockEntity vendor, ItemStack product, ItemStack price) {
        if (vendor.isDiscarding()) return true;

        ItemStack[] fakeInv = new ItemStack[9];
        for (int i = 1; i < 10; i++) {
            fakeInv[i - 1] = vendor.inventory.getStackInSlot(i).copy();
        }

        int productCount = product.getCount();
        for (int i = 0; i < 9 && productCount > 0; i++) {
            ItemStack slot = fakeInv[i];
            if (slot.isEmpty()) continue;
            if (ItemStack.isSameItemSameComponents(slot, product)) {
                int available = slot.getCount();
                int toRemove = Math.min(available, productCount);
                slot.shrink(toRemove);
                productCount -= toRemove;
            }
        }

        int remaining = price.getCount();
        for (int i = 0; i < 9 && remaining > 0; i++) {
            ItemStack slot = fakeInv[i];
            if (slot.isEmpty()) {
                int canFit = Math.min(remaining, price.getMaxStackSize());
                remaining -= canFit;
            } else if (ItemStack.isSameItemSameComponents(slot, price)) {
                int availableSpace = slot.getMaxStackSize() - slot.getCount();
                int canFit = Math.min(remaining, availableSpace);
                remaining -= canFit;
            }
        }

        fakeInv = null;
        return remaining <= 0;
    }
    
}
