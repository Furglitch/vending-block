package com.furglitch.vendingblock;

import java.util.List;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class Config {
    
    public static class Client {
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        public static final ModConfigSpec.EnumValue<AnimationMode> ANIMATION_MODE = BUILDER
            .comment("Animation mode for vending block items.\n\n'SERVER_DEFAULT' uses the server-side setting, which is 'ROTATION' unless changed by admins.\n")
            .translation("config.vendingblock.animation.mode")
            .defineEnum("clientAnimationMode", AnimationMode.SERVER_DEFAULT);

        public enum AnimationMode {
            SERVER_DEFAULT,
            STILL,
            BOBBING,
            ROTATION,
            BOBBING_ROTATION,
            FACING_PLAYER;
        }

        public static final ModConfigSpec.EnumValue<ScrolltipPosition> SCROLLTIP_POSITION = BUILDER
            .comment("Position of the 'scroll' tooltip in the vendor block GUI.\n\n'TOP' shows it at the top of the GUI,\n'ITEM_TOOLTIP' adds the scroll tooltip to the item tooltip.\n")
            .translation("config.vendingblock.scrolltip")
            .defineEnum("scrolltipPosition", ScrolltipPosition.TOP);

        public enum ScrolltipPosition {
            ITEM_TOOLTIP,
            TOP;
        }

        static { BUILDER.push("ownerNotifications"); }

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

        public static final ModConfigSpec SPEC = BUILDER.build();
    }
    
    public static class Server {
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        
        public static final ModConfigSpec.EnumValue<BreakLevel> BREAK_LEVEL = BUILDER
            .comment("Defines who besides the block owner can break the vending block.\n\nSee minecraft.wiki for more information on permission levels.\n")
            .translation("config.vendingblock.server.breakLevel")
            .defineEnum("breakLevel", BreakLevel.GAMEMASTER);

        public enum BreakLevel {
            SERVER_OWNER,
            ADMIN,
            GAMEMASTER,
            MODERATOR,
            BLOCK_OWNER_ONLY;
        }

        public static final ModConfigSpec.EnumValue<AnimationMode> ANIMATION_MODE = BUILDER
            .comment("Animation mode for vending block items\nApplies to all users who have selected 'SERVER_DEFAULT' (default) in their client settings.\n")
            .translation("config.vendingblock.animation.mode")
            .defineEnum("animationMode", AnimationMode.ROTATION);

        public enum AnimationMode {
            STILL,
            BOBBING,
            ROTATION,
            BOBBING_ROTATION,
            FACING_PLAYER;
        }
        
        @SuppressWarnings("deprecation")
        public static final ConfigValue<List<? extends String>> PRODUCT_BLACKLIST = BUILDER
            .comment("List of blacklisted facades\n\nExamples:\n- minecraft:oak_wood\n- stone\n- *stained_glass\n- #minecraft:planks\n- #c:glass_blocks\n\n(Server restart required)")
            .translation("config.vendingblock.server.productBlacklist")
            .defineListAllowEmpty("productBlacklist", java.util.Arrays.asList("vendingblock:vendor_key"), item -> item instanceof String);
        
        @SuppressWarnings("deprecation")
        public static final ConfigValue<List<? extends String>> FACADE_BLACKLIST = BUILDER
            .comment("List of blacklisted facades\n\nExamples:\n- minecraft:oak_wood\n- stone\n- *stained_glass\n- #minecraft:planks\n- #c:glass_blocks\n\n(Server restart required)")
            .translation("config.vendingblock.server.facadeBlacklist")
            .defineListAllowEmpty("facadeBlacklist", java.util.Arrays.asList("*copper_grate","#c:bookshelves","#c:glass_blocks","#minecraft:leaves","beacon","cobweb","glow_lichen","mangrove_roots","*piston","sculk_vein","vine"), item -> item instanceof String);

        public static final ModConfigSpec.BooleanValue VENDOR_KEY_IN_CREATIVE = BUILDER
            .comment("Whether the vendor key should appear in the creative mode tab (Server restart required)")
            .translation("config.vendingblock.creative.keyInTab")
            .define("vendorKeyInCreative", true);

        public static final ModConfigSpec SPEC = BUILDER.build();
    }
}


