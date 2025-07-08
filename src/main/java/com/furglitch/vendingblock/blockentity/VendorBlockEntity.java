package com.furglitch.vendingblock.blockentity;

import java.util.UUID;

import com.furglitch.vendingblock.blockentity.transaction.VendorBlockInventory;
import com.furglitch.vendingblock.gui.trade.VendorBlockMenu;
import com.furglitch.vendingblock.registry.BlockEntityRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class VendorBlockEntity extends BlockEntity implements MenuProvider{

    private UUID ownerID;
    private String ownerUser;
    public boolean infiniteInventory = false;
    public boolean hasError = false;
    public int errorCode = 0; // 0 = no error, 1 = no stock, 2 = no space, 3 = not set

    public VendorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.VENDOR_BE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu.vendingblock.settings");
    }

    public void clearContents() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
        setChanged();
    }

    public void setFilterContents(int type, ItemStack stack) {
        if (type == 1) {
            this.inventory.setStackInSlot(0, stack);
        } else if (type == 2) {
            this.inventory.setStackInSlot(10, stack);
        }
    }

    public void getFilterContents(int type) {
        if (type == 1) {
            this.inventory.getStackInSlot(0);
        } else if (type == 2) {
            this.inventory.getStackInSlot(10);
        }
    }

    public final ItemStackHandler inventory = new ItemStackHandler(12) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 64;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                level.invalidateCapabilities(getBlockPos());
                checkErrorState();
            } 
        }
    };

    private final IItemHandler insertItemHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 9;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return inventory.getStackInSlot(slot + 1);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return inventory.insertItem(slot + 1, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return inventory.getSlotLimit(slot + 1);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return inventory.isItemValid(slot + 1, stack);
        }
    };

    public IItemHandler getInsertItemHandler() {
        return insertItemHandler;
    }

    private final IItemHandler extractItemHandler = new IItemHandler() {
        @Override
        public int getSlots() {
            return 9;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return inventory.getStackInSlot(slot + 1);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack slotStack = inventory.getStackInSlot(slot + 1);
            if (slotStack.isEmpty()) {
                return ItemStack.EMPTY;
            }

            ItemStack productTemplate = inventory.getStackInSlot(0);
            
            if (!productTemplate.isEmpty() && ItemStack.isSameItemSameComponents(slotStack, productTemplate)) {
                return ItemStack.EMPTY;
            }

            return inventory.extractItem(slot + 1, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return inventory.getSlotLimit(slot + 1);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    };

    public IItemHandler getExtractItemHandler() {
        return extractItemHandler;
    }

    private final IItemHandler emptyItemHandler = new IItemHandler() {
        @Override public int getSlots() { return 0; }
        @Override public ItemStack getStackInSlot(int slot) { return ItemStack.EMPTY; }
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return stack; }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
        @Override public int getSlotLimit(int slot) { return 0;}
        @Override public boolean isItemValid(int slot, ItemStack stack) { return false; }
    };

    public IItemHandler getPublicItemHandler() {
        return emptyItemHandler;
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (i != 0 && i != 10 && i != 11) {
                inv.setItem(i, inventory.getStackInSlot(i));
            }
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public void setOwner(Player player) {
        this.ownerID = player.getUUID();
        this.ownerUser = player.getName().getString();
        setChanged();
        if (!level.isClientSide()) checkErrorState();
    }

    public void setOwnerByUsername(String username) {
        Player player = null;
        if (level != null && level.getServer() != null) {
            player = level.getServer().getPlayerList().getPlayerByName(username);
        }
        
        if (player != null) {
            this.ownerID = player.getUUID();
            this.ownerUser = player.getName().getString();
        } else {
            if (this.ownerUser != null && this.ownerUser.equals(username)) {
            } else {
                this.ownerID = null;
                this.ownerUser = username;
            }
        }
        setChanged();
        
        if(!level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            checkErrorState();
        }
    }
    
    public void updateOwnershipInfo(Player player) {
        if (this.ownerUser != null && this.ownerID == null) {
            if (this.ownerUser.equals(player.getName().getString())) {
                this.ownerID = player.getUUID();
                setChanged();
                if (!level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                    checkErrorState();
                }
            }
        }
    }

    public UUID getOwnerID() {
        return this.ownerID;
    }

    public String getOwnerUser() {
        return this.ownerUser;
    }

    public boolean hasOwner() {
        return this.ownerID != null || this.ownerUser != null;
    }
    
    public boolean isOwner(Player player) {
        if (this.ownerID != null && this.ownerID.equals(player.getUUID())) {
            return true;
        }
        
        if (this.ownerUser != null && this.ownerUser.equals(player.getName().getString())) {
            this.ownerID = player.getUUID();
            setChanged();
            return true;
        }
        
        return false;
    }

    public void setInfinite(boolean infiniteInventory) {
        this.infiniteInventory = infiniteInventory;
        setChanged();
        if (!level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            checkErrorState();
        }
    }

    public boolean isInfinite() {
        return infiniteInventory;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));

        if (this.ownerID != null) tag.putUUID("ownerID", this.ownerID);
        if (this.ownerUser != null) tag.putString("ownerUser", this.ownerUser);
        tag.putBoolean("hasError", this.hasError);
        tag.putInt("errorCode", this.errorCode);
        tag.putBoolean("infiniteInventory", this.infiniteInventory);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));

        if (tag.hasUUID("ownerID")) this.ownerID = tag.getUUID("ownerID");
        if (tag.contains("ownerUser")) this.ownerUser = tag.getString("ownerUser");
        if (tag.contains("hasError")) this.hasError = tag.getBoolean("hasError");
        if (tag.contains("errorCode")) this.errorCode = tag.getInt("errorCode");
        this.infiniteInventory = tag.getBoolean("infiniteInventory");
        
        if (level != null && !level.isClientSide()) checkErrorState();
    }

    @Override public AbstractContainerMenu createMenu(int i, Inventory inv, Player player) {
        return new VendorBlockMenu(i, inv, this);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    public void checkErrorState() {
        ItemStack product = inventory.getStackInSlot(0);
        ItemStack price = inventory.getStackInSlot(10);
        
        boolean blockHasStock = VendorBlockInventory.checkStock(this, product);
        boolean blockHasSpace = VendorBlockInventory.checkStockSpace(this, product, price);
        
        updateErrorState(blockHasStock, blockHasSpace, product, price);
    }

    public void updateErrorState(boolean blockHasStock, boolean blockHasSpace, ItemStack product, ItemStack price) {
        this.hasError = false;
        this.errorCode = 0;

        if (product.isEmpty() && price.isEmpty()) {
            this.hasError = true;
            this.errorCode = 3;
        } else {
            if (!blockHasStock) {
                this.hasError = true;
                this.errorCode = 1;
            } else if (!blockHasSpace) {
                this.hasError = true;
                this.errorCode = 2;
            }
        }
        
        setChanged();
        if (!level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

}