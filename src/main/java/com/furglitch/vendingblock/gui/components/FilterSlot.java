package com.furglitch.vendingblock.gui.components;

import java.util.List;

import com.furglitch.vendingblock.Config;
import com.furglitch.vendingblock.blockentity.DisplayBlockEntity;
import com.furglitch.vendingblock.blockentity.VendorBlockEntity;
import com.furglitch.vendingblock.gui.chat.Messages;
import com.furglitch.vendingblock.network.FilterSlotUpdatePacket;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;

public class FilterSlot extends SlotItemHandler {

    private final VendorBlockEntity vendorBlockEntity;
    private final DisplayBlockEntity displayBlockEntity;
    private final int slotIndex;

    private boolean triggeredByClick = false;

    public FilterSlot(IItemHandler handler, int index, int x, int y, VendorBlockEntity vendor) {
        super(handler, index, x, y);
        this.vendorBlockEntity = vendor;
        this.displayBlockEntity = null;
        this.slotIndex = index;
    }

    public FilterSlot(IItemHandler handler, int index, int x, int y, DisplayBlockEntity display) {
        super(handler, index, x, y);
        this.vendorBlockEntity = null;
        this.displayBlockEntity = display;
        this.slotIndex = index;
    }

    @Override
    public void set(ItemStack stack) {

        if (!stack.isEmpty()) {
            if (slotIndex == 11 && !isFullBlock(stack)) return;
            ItemStack filter = stack.copy();
            if (slotIndex == 11) {
                filter.setCount(1);
            }
            super.set(filter);

            if (vendorBlockEntity != null) {
                if (slotIndex == 0) {
                    vendorBlockEntity.setFilterContents(1, filter);
                } else if (slotIndex == 10) {
                    vendorBlockEntity.setFilterContents(2, filter);
                } else if (slotIndex == 11) {
                    vendorBlockEntity.setFilterContents(3, filter);
                }
                if (!triggeredByClick && vendorBlockEntity.getLevel() != null && vendorBlockEntity.getLevel().isClientSide()) {
                    PacketDistributor.sendToServer(new FilterSlotUpdatePacket(vendorBlockEntity.getBlockPos(), slotIndex, filter));
                }
            } else if (displayBlockEntity != null) {
                // For DisplayBlockEntity, just set the slot directly
                displayBlockEntity.inventory.setStackInSlot(slotIndex, filter);
            }
        } else {
            super.set(ItemStack.EMPTY);
            if (vendorBlockEntity != null) {
                if (slotIndex == 0) {
                    vendorBlockEntity.setFilterContents(1, ItemStack.EMPTY);
                } else if (slotIndex == 10) {
                    vendorBlockEntity.setFilterContents(2, ItemStack.EMPTY);
                } else if (slotIndex == 11) {
                    vendorBlockEntity.setFilterContents(3, ItemStack.EMPTY);
                }
                if (!triggeredByClick && vendorBlockEntity.getLevel() != null && vendorBlockEntity.getLevel().isClientSide()) {
                    PacketDistributor.sendToServer(new FilterSlotUpdatePacket(vendorBlockEntity.getBlockPos(), slotIndex, ItemStack.EMPTY));
                }
            } else if (displayBlockEntity != null) {
                displayBlockEntity.inventory.setStackInSlot(slotIndex, ItemStack.EMPTY);
            }
        }

    }
    
    private boolean isFullBlock(ItemStack stack) {

        if (!(stack.getItem() instanceof BlockItem blockItem)) return false;
        
        Block block = blockItem.getBlock();
        BlockState state = block.defaultBlockState();
        VoxelShape shape = state.getShape(null, null);
        VoxelShape fullCube = Shapes.block();
        
        return shape.equals(fullCube);
    }
    
    private boolean isBlacklisted(ItemStack stack) {
        Item item = stack.getItem();
        String itemId = item.toString();

        List<? extends String> blacklist = Config.Server.PRODUCT_BLACKLIST.get();
        if (blacklist.contains(itemId)) {
        BlockPos pos = vendorBlockEntity != null ? vendorBlockEntity.getBlockPos() : (displayBlockEntity != null ? displayBlockEntity.getBlockPos() : null);
        Player player = null;
        if (vendorBlockEntity != null && vendorBlockEntity.getLevel() != null)
            player = vendorBlockEntity.getLevel().getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 100, false);
        else if (displayBlockEntity != null && displayBlockEntity.getLevel() != null)
            player = displayBlockEntity.getLevel().getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 100, false);
        if (player != null && ((vendorBlockEntity != null && !vendorBlockEntity.getLevel().isClientSide()) || (displayBlockEntity != null && !displayBlockEntity.getLevel().isClientSide()))) {
            player.sendSystemMessage(Messages.blacklisted(stack.getHoverName().getString()));
        }
            return true;
        }
        return false;
    }
    
    public boolean onClick(ItemStack cursorStack, boolean leftClick) {
        triggeredByClick = true;
        try {
            if (leftClick) {
                if (cursorStack.isEmpty()) {
                    set(ItemStack.EMPTY);
                } else {
                    if (slotIndex == 11 && !isFullBlock(cursorStack)) return false;
                    if (isBlacklisted(cursorStack)) return false;
                    ItemStack slotStack = cursorStack.copy();
                    if (slotIndex == 11) {
                        slotStack.setCount(1);
                    } else {
                        slotStack.setCount(cursorStack.getCount());
                    }
                    set(slotStack);
                }
                return true;
            } else {
                if (cursorStack.isEmpty()) {
                    set(ItemStack.EMPTY);
                } else {
                    if (slotIndex == 11 && !isFullBlock(cursorStack)) return false;
                    if (isBlacklisted(cursorStack)) return false;
                    ItemStack slotStack = cursorStack.copy();
                    if (slotIndex == 11 || cursorStack.getItem() != getItem().getItem()) {
                        slotStack.setCount(1);
                    } else {
                        slotStack.setCount(getItem().getCount() + 1);
                    }
                    set(slotStack);
                }
                return true;
            }
        } finally {
            triggeredByClick = false;
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
        if (displayBlockEntity != null && slotIndex == 1) {
            return true;
        } else { return false; }
    }
    
    public int getSlotIndex() {
        return this.slotIndex;
    }
}
