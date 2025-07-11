package com.furglitch.vendingblock.registry;

import java.util.function.Supplier;

import com.furglitch.vendingblock.VendingBlock;
import com.furglitch.vendingblock.blockentity.VendorBlockEntity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, VendingBlock.MODID);

    public static final Supplier<BlockEntityType<VendorBlockEntity>> VENDOR_BE = 
        BLOCK_ENTITIES.register("vending_block_be", () -> BlockEntityType.Builder.of(
            VendorBlockEntity::new, 
            BlockRegistry.VENDOR.get()).build(null)
        );

    public static final Supplier<BlockEntityType<com.furglitch.vendingblock.blockentity.DisplayBlockEntity>> DISPLAY_BE =
        BLOCK_ENTITIES.register("display_block_be", () -> BlockEntityType.Builder.of(
            com.furglitch.vendingblock.blockentity.DisplayBlockEntity::new,
            BlockRegistry.DISPLAY.get()).build(null)
        );
    
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}