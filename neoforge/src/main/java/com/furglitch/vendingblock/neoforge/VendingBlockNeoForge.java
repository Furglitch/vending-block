package com.furglitch.vendingblock.neoforge;

import com.furglitch.vendingblock.VendingBlock;

import net.neoforged.fml.common.Mod;

@Mod(VendingBlock.MOD_ID)
public final class VendingBlockNeoForge {

    public VendingBlockNeoForge() {
        VendingBlock.init();
    }
}
