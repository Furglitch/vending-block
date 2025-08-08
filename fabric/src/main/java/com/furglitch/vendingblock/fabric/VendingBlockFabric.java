package com.furglitch.vendingblock.fabric;

import com.furglitch.vendingblock.VendingBlock;

import net.fabricmc.api.ModInitializer;

public final class VendingBlockFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        VendingBlock.init();
    }
}
