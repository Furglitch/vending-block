package com.furglitch.vendingblock.gui.components;

import java.util.List;

import com.furglitch.vendingblock.Config;
import com.furglitch.vendingblock.blockentity.DisplayBlockEntity;
import com.furglitch.vendingblock.blockentity.VendorBlockEntity;
import com.furglitch.vendingblock.gui.chat.Messages;
import com.furglitch.vendingblock.network.FilterSlotUpdatePacket;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

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
    
    public boolean onClick(ItemStack cursorStack, boolean leftClick) {
        triggeredByClick = true;
        try {
            if (vendorBlockEntity != null) {
                if (leftClick) {
                    if (cursorStack.isEmpty()) {
                        set(ItemStack.EMPTY);
                    } else {
                        if (slotIndex == 11 && (isBlacklistedFacade(cursorStack) || !isFullBlock(cursorStack))) return false;
                        if (slotIndex == 0 && isBlacklistedProduct(cursorStack)) return false;
                        ItemStack slotStack = cursorStack.copy();
                        if (slotIndex == 11) {
                            slotStack.setCount(1);
                        } else {
                            slotStack.setCount(cursorStack.getCount());
                        }
                        set(slotStack);
                    }
                } else {
                    if (cursorStack.isEmpty()) {
                        set(ItemStack.EMPTY);
                    } else {
                        if (slotIndex == 11 && (isBlacklistedFacade(cursorStack) || !isFullBlock(cursorStack))) return false;
                        if (slotIndex == 0 && isBlacklistedProduct(cursorStack)) return false;
                        ItemStack slotStack = cursorStack.copy();
                        if (slotIndex == 11 || cursorStack.getItem() != getItem().getItem()) {
                            slotStack.setCount(1);
                        } else {
                            slotStack.setCount(getItem().getCount() + 1);
                        }
                        set(slotStack);
                    }
                }
            } else if (displayBlockEntity != null) {
                if (cursorStack.isEmpty()) {
                    set(ItemStack.EMPTY);
                } else {
                    if (isBlacklistedFacade(cursorStack) || !isFullBlock(cursorStack)) {
                        cursorStack = ItemStack.EMPTY;
                        return false;
                    }
                    ItemStack slotStack = cursorStack.copy();
                    slotStack.setCount(1);
                    set(slotStack);
                }
            }
            return true;
        } finally {
            triggeredByClick = false;
        }
    }
    
    private boolean isFullBlock(ItemStack stack) {

        if (!(stack.getItem() instanceof BlockItem blockItem)) return false;

        Block block = blockItem.getBlock();
        BlockState state = block.defaultBlockState();
        net.minecraft.world.level.BlockGetter blockGetter = null;
        BlockPos pos = null;
        if (vendorBlockEntity != null && vendorBlockEntity.getLevel() != null) {
            blockGetter = vendorBlockEntity.getLevel();
            pos = vendorBlockEntity.getBlockPos();
        } else if (displayBlockEntity != null && displayBlockEntity.getLevel() != null) {
            blockGetter = displayBlockEntity.getLevel();
            pos = displayBlockEntity.getBlockPos();
        }

        VoxelShape shape;
        try {
            if (blockGetter != null && pos != null) {
                shape = state.getShape(blockGetter, pos);
            } else {
                shape = state.getShape(null, null);
            }
        } catch (Exception e) {
            return false;
        }

        VoxelShape fullCube = Shapes.block();
        if (!shape.equals(fullCube)) {
            if (pos == null) { return false; }
            Player player = null;
            if (vendorBlockEntity != null && vendorBlockEntity.getLevel() != null)
                player = vendorBlockEntity.getLevel().getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 100, false);
            else if (displayBlockEntity != null && displayBlockEntity.getLevel() != null)
                player = displayBlockEntity.getLevel().getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 100, false);
            if (player != null && ((vendorBlockEntity != null && !vendorBlockEntity.getLevel().isClientSide()) || (displayBlockEntity != null && !displayBlockEntity.getLevel().isClientSide()))) {
                player.sendSystemMessage(Messages.fullBlockFacade(stack.getHoverName().getString()));
            }
            return false;
        }
        return true;
    }
    
    private boolean isBlacklistedProduct(ItemStack stack) {
        Item item = stack.getItem();
        String itemId = item.toString();
        List<? extends String> blacklist = Config.Server.PRODUCT_BLACKLIST.get();
        boolean match = false;

        for (String entry : blacklist) {
            System.out.println("Checking " + entry + " against " + itemId);
            if (entry.equals(itemId) || ("minecraft:"+entry).equals(itemId)) { match = true; }
            else if (entry.startsWith("#")) { match = isItemInTag(entry.substring(1), stack); }
            if (match) break;
        }

        if (match) {
            BlockPos pos = vendorBlockEntity != null ? vendorBlockEntity.getBlockPos() : (displayBlockEntity != null ? displayBlockEntity.getBlockPos() : null);
            Player player = null;
            if ( vendorBlockEntity != null && vendorBlockEntity.getLevel() != null ) {
                player = vendorBlockEntity.getLevel().getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 100, false);
            }
            if ( player != null && ((vendorBlockEntity != null && !vendorBlockEntity.getLevel().isClientSide())) ) {
                player.sendSystemMessage(Messages.blacklistedFacade(stack.getHoverName().getString()));
            }
        }

        return match;
    }
    
    private boolean isBlacklistedFacade(ItemStack stack) {
        Item item = stack.getItem();
        String itemId = item.toString();
        List<? extends String> blacklist = Config.Server.FACADE_BLACKLIST.get();
        boolean match = false;

        for (String entry : blacklist) {
            System.out.println("Checking " + entry + " against " + itemId);
            if (entry.equals(itemId)) { match = true; }
            else if (entry.startsWith("#")) { match = isItemInTag(entry.substring(1), stack); }
            if (match) break;
        }

        if (match) {
            BlockPos pos = vendorBlockEntity != null ? vendorBlockEntity.getBlockPos() : (displayBlockEntity != null ? displayBlockEntity.getBlockPos() : null);
            Player player = null;
            if (vendorBlockEntity != null && vendorBlockEntity.getLevel() != null) {
                player = vendorBlockEntity.getLevel().getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 100, false);
            } else if (displayBlockEntity != null && displayBlockEntity.getLevel() != null) {
                player = displayBlockEntity.getLevel().getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 100, false);
            }
            if (player != null && ((vendorBlockEntity != null && !vendorBlockEntity.getLevel().isClientSide()) || (displayBlockEntity != null && !displayBlockEntity.getLevel().isClientSide()))) {
                player.sendSystemMessage(Messages.blacklistedFacade(stack.getHoverName().getString()));
            }
        }

        return match;
    }

    @SuppressWarnings("deprecation")
    public static boolean isItemInTag(String tagString, ItemStack stack) {
        ResourceLocation tagId = ResourceLocation.tryParse(tagString);
        if (tagId == null) { return false;}

        TagKey<Item> itemTagKey = TagKey.create(Registries.ITEM, tagId);
        TagKey<Block> blockTagKey = TagKey.create(Registries.BLOCK, tagId);

        Level level = null;
        if (ServerLifecycleHooks.getCurrentServer() != null) { level = ServerLifecycleHooks.getCurrentServer().overworld(); }

        if (level != null) {
            RegistryAccess registryAccess = level.registryAccess();
            var itemRegistry = registryAccess.registryOrThrow(Registries.ITEM);
            var itemTag = itemRegistry.getTag(itemTagKey);
            boolean itemTagExists = itemTag != null;
            if (itemTagExists) {
                boolean result = stack.getItem().builtInRegistryHolder().is(itemTagKey);
                if (result) return true;
            }

            if (stack.getItem() instanceof BlockItem blockItem) {
                var blockRegistry = registryAccess.registryOrThrow(Registries.BLOCK);
                var blockTag = blockRegistry.getTag(blockTagKey);
                boolean blockTagExists = blockTag != null;
                if (blockTagExists) {
                    boolean result = blockItem.getBlock().builtInRegistryHolder().is(blockTagKey);
                    if (result) return true;
                }
            }
        }
        return false;
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
    
    public int getSlotIndex() {
        return this.slotIndex;
    }
}
