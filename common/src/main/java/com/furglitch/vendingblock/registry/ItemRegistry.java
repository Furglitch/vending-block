package com.furglitch.vendingblock.registry;

import java.util.function.Supplier;

import com.furglitch.vendingblock.VendingBlock;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(VendingBlock.MOD_ID, Registries.ITEM);

    public static RegistrySupplier<Item> VENDOR_ITEM = registerItem(
        "vending_block",
        () -> new BlockItem(
            BlockRegistry.VENDOR.get(),
            defaultProperties("vending_block").arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)
        )
    );

    public static RegistrySupplier<Item> DISPLAY_ITEM = registerItem(
        "display_block",
        () -> new BlockItem(
            BlockRegistry.DISPLAY.get(),
            defaultProperties("display_block").arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)
        )
    );

    public static RegistrySupplier<Item> KEY = registerItem(
        "vendor_key",
        () -> new Item(defaultProperties("vendor_key").arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS))
    );

    public static void init() {
        ITEMS.register();
    }

    public static RegistrySupplier<Item> registerItem(String name, Supplier<Item> item) {
        return ITEMS.register(
            ResourceLocation.fromNamespaceAndPath(VendingBlock.MOD_ID, name),
            item
        );
    }

    public static Item.Properties defaultProperties(String name) {
        return new Item.Properties();
    }

}
