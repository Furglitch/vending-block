package com.furglitch.vendingblock.neoforge;

import net.neoforged.fml.common.Mod;

import com.furglitch.vendingblock.VendingBlock;

@Mod(VendingBlock.MOD_ID)
public final class VendingBlockNeoForge {
    public VendingBlockNeoForge() {
        // Run our common setup.
        VendingBlock.init();
    }
}
