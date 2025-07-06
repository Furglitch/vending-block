package com.furglitch.vendingblock.registry;

import com.furglitch.vendingblock.VendingBlock;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = 
        DeferredRegister.createItems(VendingBlock.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
    
}
