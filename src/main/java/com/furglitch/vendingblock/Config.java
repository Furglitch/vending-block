package com.furglitch.vendingblock;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static { BUILDER.push("ownedNotifications"); }

    public static final ModConfigSpec.BooleanValue PURCHASE_MESSAGES = BUILDER
        .comment("Whether to receive messages when players purchase items from your vending blocks")
        .translation("config.vendingblock.messages.purchase")
        .define("purchaseMessages", true);

    public static final ModConfigSpec.BooleanValue GIVEAWAY_MESSAGES = BUILDER
        .comment("Whether to receive messages when players take free items from your vending blocks")
        .translation("config.vendingblock.messages.giveaway")
        .define("giveawayMessages", true);

    public static final ModConfigSpec.BooleanValue DONATION_MESSAGES = BUILDER
        .comment("Whether to receive messages when players donate items to your vending blocks")
        .translation("config.vendingblock.messages.donation")
        .define("donationMessages", true);

    public static final ModConfigSpec.BooleanValue OUT_OF_STOCK_MESSAGES = BUILDER
        .comment("Whether to receive messages when your vending blocks run out of stock")
        .translation("config.vendingblock.messages.outOfStock")
        .define("outOfStockMessages", true);

    public static final ModConfigSpec.BooleanValue FULL_STORAGE_MESSAGES = BUILDER
        .comment("Whether to receive messages when your vending blocks' storage becomes full")
        .translation("config.vendingblock.messages.fullStorage")
        .define("fullStorageMessages", true);

    static { BUILDER.pop(); }

    static final ModConfigSpec SPEC = BUILDER.build();
        
}
