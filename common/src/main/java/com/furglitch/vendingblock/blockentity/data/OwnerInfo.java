package com.furglitch.vendingblock.blockentity.data;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class OwnerInfo {
    public UUID ownerId = null;
    public String ownerUser = null;

    public OwnerInfo() {}

    public OwnerInfo(UUID ownerId, String ownerUser) {
        this.ownerId = ownerId;
        this.ownerUser = ownerUser;
    }

    public void setOwner(Player player) {
        this.ownerId = player.getUUID();
        this.ownerUser = player.getName().getString();
    }

    public void setOwner(String user, Level level) {
        Player player = level.getServer().getPlayerList().getPlayerByName(user) != null ? 
            level.getServer().getPlayerList().getPlayerByName(user) : null;

        if (player != null) {
            setOwner(player);
        } else {
            this.ownerId = null;
            this.ownerUser = user;
        }
    }

    public boolean hasOwner() {
        return this.ownerId != null;
    }

    public boolean isOwner(Player player) {
        return player != null && player.getUUID().equals(this.ownerId);
    }

    public UUID getOwnerId() {
        return this.ownerId;
    }

    public String getOwnerUser() {
        return this.ownerUser;
    }

    public void saveNBT(CompoundTag tag) {
        if (ownerId != null) tag.putUUID("ownerID", this.ownerId);
        if (ownerUser != null) tag.putString("ownerUser", this.ownerUser);
    }

    public void loadNBT(CompoundTag tag) {
        if (tag.hasUUID("ownerID")) this.ownerId = tag.getUUID("ownerID");
        if (tag.contains("ownerUser")) this.ownerUser = tag.getString("ownerUser");
    }

}
