package com.furglitch.vendingblock.gui.chat;

import net.minecraft.network.chat.Component;

public class Messages {

    public static Component playerBought(int count, String item, String owner, int sellCount, String sellItem) {
        return Component.translatable("msg.vendingblock.sell", count, item, owner, sellCount, sellItem);
    }

    public static Component playerRequest(int count, String item, String owner) {
        return Component.translatable("msg.vendingblock.request", count, item, owner);
    }

    public static Component playerGiveaway(int count, String item, String owner) {
        return Component.translatable("msg.vendingblock.giveaway", count, item, owner);
    }

    public static Component playerEmpty(String sellItem) {
        return Component.translatable("msg.vendingblock.empty.player", sellItem);
    }

    public static Component playerFull() {
        return Component.translatable("msg.vendingblock.full.player");
    }

    public static Component vendorFull() {
        return Component.translatable("msg.vendingblock.full");
    }

    public static Component vendorSold() {
        return Component.translatable("msg.vendingblock.sold");
    }

    public static Component vendorEmpty() {
        return Component.translatable("msg.vendingblock.empty");
    }

    public static Component ownerSold(int count, String item, String player, int sellCount, String sellItem) {
        return Component.translatable("msg.vendingblock.sell.owner", count, item, player, sellCount, sellItem);
    }

    public static Component ownerRequest(int count, String item, String player) {
        return Component.translatable("msg.vendingblock.request.owner", count, item, player);
    }

    public static Component ownerGiveaway(int count, String item, String player) {
        return Component.translatable("msg.vendingblock.giveaway.owner", count, item, player);
    }

    public static Component ownerSold() {
        return Component.translatable("msg.vendingblock.sold.owner");
    }

    public static Component ownerFull() {
        return Component.translatable("msg.vendingblock.full.owner");
    }
    
}
