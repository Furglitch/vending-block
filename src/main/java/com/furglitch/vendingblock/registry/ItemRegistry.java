package com.furglitch.vendingblock.registry;

import com.furglitch.vendingblock.VendingBlock;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(VendingBlock.MODID);

    public static final DeferredItem<Item> VENDOR_KEY = ITEMS.register("vendor_key",
        () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
    
}
