package com.furglitch.vendingblock.registry;

import java.util.function.Supplier;

import com.furglitch.vendingblock.VendingBlock;
import com.furglitch.vendingblock.blockentity.DisplayBlockEntity;
import com.furglitch.vendingblock.blockentity.VendorBlockEntity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(VendingBlock.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    @SuppressWarnings("unchecked")
    public static final RegistrySupplier<BlockEntityType<VendorBlockEntity>> VENDOR_BE = (RegistrySupplier<BlockEntityType<VendorBlockEntity>>) (RegistrySupplier<?>) BLOCK_ENTITIES.register(
        "vending_block_be",
        () -> {
            return (BlockEntityType<?>) BlockEntityType.Builder.of(
                (BlockEntityType.BlockEntitySupplier<VendorBlockEntity>) VendorBlockEntity::new,
                BlockRegistry.VENDOR.get()
            ).build(null);
        }
    );

    @SuppressWarnings("unchecked")
    public static final RegistrySupplier<BlockEntityType<DisplayBlockEntity>> DISPLAY_BE = (RegistrySupplier<BlockEntityType<DisplayBlockEntity>>) (RegistrySupplier<?>) BLOCK_ENTITIES.register(
        "display_block_be",
        () -> {
            return (BlockEntityType<?>) BlockEntityType.Builder.of(
                (BlockEntityType.BlockEntitySupplier<DisplayBlockEntity>) DisplayBlockEntity::new,
                BlockRegistry.DISPLAY.get()
            ).build(null);
        }
    );

    public static void init() {
        BLOCK_ENTITIES.register();
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntityType<?>> RegistrySupplier<T> register(String name, Supplier<? extends T> blockEntity) {
        return (RegistrySupplier<T>) BLOCK_ENTITIES.register(
            ResourceLocation.fromNamespaceAndPath(VendingBlock.MOD_ID, name),
            blockEntity
        );
    }

}
