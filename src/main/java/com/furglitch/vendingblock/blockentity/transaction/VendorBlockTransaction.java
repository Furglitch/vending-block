package com.furglitch.vendingblock.blockentity.transaction;

import com.furglitch.vendingblock.Config;
import com.furglitch.vendingblock.blockentity.VendorBlockEntity;
import com.furglitch.vendingblock.gui.chat.Messages;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VendorBlockTransaction {

    public static void purchase(Level level, Player buyer, VendorBlockEntity vendor) {
        ItemStack product = vendor.inventory.getStackInSlot(0);
        ItemStack price = vendor.inventory.getStackInSlot(10);
        if (product.isEmpty() && price.isEmpty()) return;

        Player owner = vendor.getOwnerID() != null ? level.getPlayerByUUID(vendor.getOwnerID()) : null;
        String ownerName = vendor.getOwnerUser();
        String playerName = buyer.getName().getString();
        boolean playerHasPayment = VendorBlockInventory.checkInventory(buyer, price);
        boolean playerHasSpace = VendorBlockInventory.checkInventorySpace(buyer, price, product);
        boolean blockHasStock = VendorBlockInventory.checkStock(vendor, product);
        boolean blockHasSpace = VendorBlockInventory.checkStockSpace(vendor, product, price);

        if (playerHasPayment && playerHasSpace && blockHasStock && blockHasSpace && !product.isEmpty() && !price.isEmpty()) { // trade
            giveProduct(buyer, vendor, product);
            recievePayment(buyer, vendor, price);
            buyer.sendSystemMessage(Messages.playerBought(product.getCount(), product.getHoverName(), ownerName, price.getCount(), price.getHoverName()));
            if (owner != null && Config.PURCHASE_MESSAGES.get()) owner.sendSystemMessage(Messages.ownerSold(product.getCount(), product.getHoverName(), playerName, price.getCount(), price.getHoverName()));

        } else if (playerHasSpace && blockHasStock && !product.isEmpty() && price.isEmpty()) { // giveaway 
            giveProduct(buyer, vendor, product);
            buyer.sendSystemMessage(Messages.playerGiveaway(product.getCount(), product.getHoverName(), ownerName));
            if (owner != null && Config.GIVEAWAY_MESSAGES.get()) owner.sendSystemMessage(Messages.ownerGiveaway(product.getCount(), product.getHoverName(), playerName));

        } else if (playerHasPayment && blockHasSpace && product.isEmpty() && !price.isEmpty()) { // donation
            recievePayment(buyer, vendor, price);
            buyer.sendSystemMessage(Messages.playerRequest(price.getCount(), price.getHoverName(), ownerName));
            if (owner != null && Config.DONATION_MESSAGES.get()) owner.sendSystemMessage(Messages.ownerRequest(price.getCount(), price.getHoverName(), playerName));

        } else if (!playerHasPayment) {
            buyer.sendSystemMessage(Messages.playerEmpty(price.getHoverName()));

        } else if (!playerHasSpace) {
            buyer.sendSystemMessage(Messages.playerFull());

        } else if (!blockHasStock) {
            buyer.sendSystemMessage(Messages.vendorSold());
            if (owner != null && Config.OUT_OF_STOCK_MESSAGES.get()) owner.sendSystemMessage(Messages.ownerSold());

        } else if (!blockHasSpace) {
            buyer.sendSystemMessage(Messages.vendorFull());
            if (owner != null && Config.FULL_STORAGE_MESSAGES.get()) owner.sendSystemMessage(Messages.ownerFull());

        }
        
        vendor.checkErrorState();

        return;
    }

    private static void recievePayment(Player buyer, VendorBlockEntity vendor, ItemStack price) {

        int cost = price.getCount();
        for (int i = 0; i < buyer.getInventory().getContainerSize() && cost > 0; i++) {
            ItemStack slot = buyer.getInventory().getItem(i);
            if (!slot.isEmpty() && ItemStack.isSameItemSameComponents(slot, price)) {
                int toRemove = Math.min(slot.getCount(), cost);
                slot.shrink(toRemove);
                cost -= toRemove;
            }
        }

        int transfer = price.getCount() - cost;
        for (int i = 1; i <= 9 && transfer > 0; i++) {
            ItemStack slot = vendor.inventory.getStackInSlot(i);
            if (slot.isEmpty()) {
                int space = Math.min(transfer, price.getMaxStackSize());
                ItemStack insert = price.copy();
                insert.setCount(space);
                vendor.inventory.setStackInSlot(i, insert);
                transfer -= space;
            } else if (ItemStack.isSameItemSameComponents(slot, price)) {
                int space = slot.getMaxStackSize() - slot.getCount();
                int freeSpace = Math.min(space, transfer);
                if (freeSpace > 0) {
                    slot.grow(freeSpace);
                    transfer -= freeSpace;
                }
            }
        }
    }

    private static void giveProduct(Player buyer, VendorBlockEntity vendor, ItemStack product) {
        int stock = product.getCount();
        if (vendor.isInfinite()) {
            stock = 0;
        } else {
            for (int i = 1; i <= 9 && stock > 0; i++) {
                ItemStack slot = vendor.inventory.getStackInSlot(i);
                if (slot.isEmpty()) continue;
                if (ItemStack.isSameItemSameComponents(slot, product)) {
                    int available = slot.getCount();
                    int slotStock = Math.min(available, stock);
                    slot.shrink(slotStock);
                    stock -= slotStock;
                }
            }
        }

        int transfer = product.getCount() - stock;
        for (int i = 0; i < 36 && transfer > 0; i++) {
            ItemStack slot = buyer.getInventory().getItem(i);
            if (slot.isEmpty()) {
                int space = Math.min(transfer, product.getMaxStackSize());
                ItemStack insert = product.copy();
                insert.setCount(space);
                buyer.getInventory().setItem(i, insert);
                transfer -= space;
            } else if (ItemStack.isSameItemSameComponents(slot, product)) {
                int space = slot.getMaxStackSize() - slot.getCount();
                int freeSpace = Math.min(space, transfer);
                if (freeSpace > 0) {
                    slot.grow(freeSpace);
                    transfer -= freeSpace;
                }
                
            }
        }
    }
    
}
