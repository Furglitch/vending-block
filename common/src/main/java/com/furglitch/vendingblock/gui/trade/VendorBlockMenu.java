package com.furglitch.vendingblock.gui.trade;

import com.furglitch.vendingblock.blockentity.VendorBlockEntity;
import com.furglitch.vendingblock.gui.components.GhostFilterSlot;
import com.furglitch.vendingblock.registry.MenuRegistry;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
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
        
        if (cont instanceof VendorBlockEntity be) {
            this.addSlot(new GhostFilterSlot(be, 0, 26, 17));
            this.addSlot(new GhostFilterSlot(be, 1, 26, 53));
            this.addSlot(new GhostFilterSlot(be, 2, 134, 17));
        } else {
            this.addSlot(new GhostFilterSlot(null, 0, 26, 17));
            this.addSlot(new GhostFilterSlot(null, 1, 26, 53));
            this.addSlot(new GhostFilterSlot(null, 2, 134, 17));
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
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack original = slot.getItem();
            result = original.copy();

            int containerSlots = CONTAINER_INVENTORY_ROW_COUNT * CONTAINER_INVENTORY_COLUMN_COUNT;
            int ghostSlots = 3;
            int totalContainer = containerSlots + ghostSlots;

            if (index >= 0 && index < containerSlots) { // Block Inventory
                if (!this.moveItemStackTo(original, totalContainer, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onTake(player, original);
            }

            else if (index >= totalContainer && index < this.slots.size()) { // Player Inventory
                if (!this.moveItemStackTo(original, 0, containerSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }

            else if (index >= containerSlots && index < totalContainer) { // Ghost Slots
                return ItemStack.EMPTY;
            }

            if (original.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (original.getCount() == result.getCount()) { return ItemStack.EMPTY; }

            slot.onTake(player, original);
        }

        return result;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (this.container instanceof VendorBlockEntity) {
            if (slotId >= 0 && slotId < this.slots.size()) {
                Slot slot = this.slots.get(slotId);
                if (slot instanceof GhostFilterSlot ghost) {
                    ItemStack carried = player.containerMenu != null ? player.containerMenu.getCarried() : ItemStack.EMPTY;
                    if (carried == null) carried = ItemStack.EMPTY;
                    ItemStack toSet = carried.isEmpty() ? ItemStack.EMPTY : carried.copy();
                    if (!toSet.isEmpty()) toSet.setCount(1);
                    ghost.set(toSet);
                    return;
                }
            }
        }
        super.clicked(slotId, button, clickType, player);
    }
    
}
