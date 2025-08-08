package com.furglitch.vendingblock.registry;

import java.util.function.Supplier;

import com.furglitch.vendingblock.VendingBlock;
import com.furglitch.vendingblock.block.DisplayBlock;
import com.furglitch.vendingblock.block.VendorBlock;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;



public class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(VendingBlock.MOD_ID, Registries.BLOCK);

    public static final RegistrySupplier<Block> VENDOR = register(
        "vending_block", 
        () -> new VendorBlock(defaultProperties("vending_block"))
    );

    public static final RegistrySupplier<Block> DISPLAY = register(
        "display_block", 
        () -> new DisplayBlock(defaultProperties("display_block"))
    );

    public static void init() {
        BLOCKS.register();
    }

    public static RegistrySupplier<Block> register(String name, Supplier<Block> block) {
        return BLOCKS.register(
            ResourceLocation.fromNamespaceAndPath(VendingBlock.MOD_ID, name),
            block
        );
    }

    public static Block.Properties defaultProperties(String name) {
        return Block.Properties.of()
            .strength(1.5f)
            .sound(SoundType.METAL)
            .noOcclusion();
    }
}