package com.furglitch.vendingblock.blockentity;

import java.util.UUID;

import com.furglitch.vendingblock.gui.chat.Messages;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class VendorBlockEntity extends BlockEntity implements MenuProvider{

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
            } 
        }
    };

    public VendorBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.VENDOR_BE.get(), pos, blockState);
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

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    private UUID ownerID;
    private String ownerUser;

    public void setOwner(Player player) {
        this.ownerID = player.getUUID();
        this.ownerUser = player.getName().getString();
        setChanged();
    }

    public UUID getOwnerID() {
        return this.ownerID;
    }

    public String getOwnerUser() {
        return this.ownerUser;
    }
    
    public boolean isOwner(Player player) {
        return this.ownerID != null && this.ownerID.equals(player.getUUID());
    }
    
    public boolean hasOwner() {
        return this.ownerID != null;
    }

    public static void purchase(Level level, Player buyer, VendorBlockEntity vendor) {
        ItemStack product = vendor.inventory.getStackInSlot(0);
        ItemStack price = vendor.inventory.getStackInSlot(10);
        if (product.isEmpty() && price.isEmpty()) return;

        Player owner = level.getPlayerByUUID(vendor.getOwnerID());
        String ownerName = vendor.getOwnerUser();
        String playerName = buyer.getName().getString();
        boolean playerHasPayment = checkInventory(buyer, price);
        boolean playerHasSpace = checkInventorySpace(buyer, price, product);
        boolean blockHasStock = checkStock(vendor, product);
        boolean blockHasSpace = checkStockSpace(vendor, product, price);
        buyer.sendSystemMessage(Component.literal(playerHasPayment + " " + playerHasSpace + " " + blockHasStock + " " + blockHasSpace));

        if (playerHasPayment && playerHasSpace && blockHasStock && blockHasSpace && !product.isEmpty() && !price.isEmpty()) { // trade
            giveProduct(buyer, vendor, product);
            recievePayment(buyer, vendor, price);
            buyer.sendSystemMessage(Messages.playerBought(product.getCount(), product.getHoverName().getString(), ownerName, price.getCount(), price.getHoverName().getString()));
            if (owner != null) owner.sendSystemMessage(Messages.ownerSold(product.getCount(), product.getHoverName().getString(), ownerName, price.getCount(), price.getHoverName().getString()));
            return;
        } else if (playerHasSpace && blockHasStock && !product.isEmpty() && price.isEmpty()) { // giveaway 
            giveProduct(buyer, vendor, product);
            buyer.sendSystemMessage(Messages.playerGiveaway(product.getCount(), product.getHoverName().getString(), ownerName));
            if (owner != null) owner.sendSystemMessage(Messages.ownerGiveaway(product.getCount(), product.getHoverName().getString(), playerName));
            return;
        } else if (playerHasPayment && blockHasSpace && product.isEmpty() && !price.isEmpty()) { // donation
            recievePayment(buyer, vendor, price);
            buyer.sendSystemMessage(Messages.playerRequest(price.getCount(), price.getHoverName().getString(), ownerName));
            if (owner != null) owner.sendSystemMessage(Messages.ownerRequest(price.getCount(), price.getHoverName().getString(), playerName));
            return;
        } else if (!playerHasPayment) {
            buyer.sendSystemMessage(Messages.playerEmpty(price.getHoverName().getString()));
            return;
        } else if (!playerHasSpace) {
            buyer.sendSystemMessage(Messages.playerFull());
            return;
        } else if (!blockHasStock) {
            buyer.sendSystemMessage(Messages.vendorSold());
            if (owner != null) owner.sendSystemMessage(Messages.ownerSold());
            return;
        } else if (!blockHasSpace) {
            buyer.sendSystemMessage(Messages.vendorFull());
            if (owner != null) owner.sendSystemMessage(Messages.ownerFull());
            return;
        }

    }

    private static boolean checkInventory(Player buyer, ItemStack price) {

        int inv = 0;
        for ( int i = 0; i < buyer.getInventory().getContainerSize(); i++) {
            ItemStack slot = buyer.getInventory().getItem(i);
            if (slot.getItem().equals(price.getItem())) {
                inv += slot.getCount();
            }
        }
        return inv >= price.getCount();
    }
    
    private static boolean checkInventorySpace(Player buyer, ItemStack price, ItemStack product) {

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

    private static boolean checkStock(VendorBlockEntity vendor, ItemStack product) {

        int stock = 0;
        for (int i = 1; i <= 9; i++) {
            ItemStack slot = vendor.inventory.getStackInSlot(i);
            if (slot.getItem().equals(product.getItem())) {
                stock += slot.getCount();
            }
        }
        return stock >= product.getCount();

    }

    private static boolean checkStockSpace(VendorBlockEntity vendor, ItemStack product, ItemStack price) {

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

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));

        if (this.ownerID != null) tag.putUUID("ownerID", this.ownerID);
        if (this.ownerUser != null) tag.putString("ownerUser", this.ownerUser);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));

        if (tag.hasUUID("ownerID")) this.ownerID = tag.getUUID("ownerID");
        if (tag.contains("ownerUser")) this.ownerUser = tag.getString("ownerUser");
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Vendor");
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

}