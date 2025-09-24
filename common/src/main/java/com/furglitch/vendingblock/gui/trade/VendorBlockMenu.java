package com.furglitch.vendingblock.gui.trade;

import com.furglitch.vendingblock.blockentity.VendorBlockEntity;
import com.furglitch.vendingblock.registry.MenuRegistry;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class VendorBlockMenu extends AbstractContainerMenu {

    public final Container container;
    public final ContainerData data;

    public VendorBlockMenu(int id, Inventory inv) {
        this(id, inv, new SimpleContainer(9), new SimpleContainerData(9));
    }

    public VendorBlockMenu(int id, Inventory inv, Container container, ContainerData data) {
        super(MenuRegistry.VENDOR_MENU.get(), id);
        checkContainerSize(container, 9);
        checkContainerDataCount(data, 9);
        this.container = container;
        this.data = data;

        addContainerInventory(container);
        addPlayerInventory(inv);
        this.addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.container instanceof VendorBlockEntity be) {
            if (be.getLevel() == null) return false;
            double dx = player.getX() - (be.getBlockPos().getX() + 0.5);
            double dy = player.getY() - (be.getBlockPos().getY() + 0.5);
            double dz = player.getZ() - (be.getBlockPos().getZ() + 0.5);
            double distSq = dx * dx + dy * dy + dz * dz;
            return distSq <= 64.0; // 8 blocks
        }
        return true;
    }

    private static final int CONTAINER_INVENTORY_ROW_COUNT = 3;
    private static final int CONTAINER_INVENTORY_COLUMN_COUNT = 3;
    private void addContainerInventory(Container cont) {
        for (int i = 0; i < CONTAINER_INVENTORY_ROW_COUNT; i++) {
            for (int j = 0; j < CONTAINER_INVENTORY_COLUMN_COUNT; j++) {
                int slotIndex = j + i * 3;
                this.addSlot(new Slot(cont, slotIndex, 62 + (j * 18), 17 + (i * 18)) {
                    @Override
                    public void set(ItemStack stack) {
                        super.set(stack);
                        notifyContainerChanged(cont);
                    }

                    @Override
                    public void onTake(Player player, ItemStack stack) {
                        super.onTake(player, stack);
                        notifyContainerChanged(cont);
                    }
                });
            }
        }
    }

    private void notifyContainerChanged(Container cont) {
        if (cont instanceof VendorBlockEntity be) {
            be.setChanged();
            if (be.getLevel() != null) {
                be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
            }
        }
    }

    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int HOTBAR_SLOT_COUNT = 9;
    private void addPlayerInventory(Inventory inv) {
        for (int i = 0; i < PLAYER_INVENTORY_ROW_COUNT; i++) {
            for (int j = 0; j < PLAYER_INVENTORY_COLUMN_COUNT; j++) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < HOTBAR_SLOT_COUNT; i++) {
            this.addSlot(new Slot(inv, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack sourceStack = slot.getItem();
            stack = sourceStack.copy();

            if (sourceStack.isEmpty()) { slot.set(ItemStack.EMPTY); }
            else { slot.setChanged(); }

            slot.onTake(player, sourceStack);
        }
        return stack;
    }
    
}
