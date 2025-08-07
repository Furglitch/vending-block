package com.furglitch.vendingblock.fabric;

import com.furglitch.vendingblock.VendingBlock;
import com.furglitch.vendingblock.fabric.config.ClientConfig;
import com.furglitch.vendingblock.fabric.config.ServerConfig;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.fabricmc.api.ModInitializer;

public final class VendingBlockFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ConfigApiJava.registerAndLoadConfig(ClientConfig::new, RegisterType.CLIENT);
        ConfigApiJava.registerAndLoadConfig(ServerConfig::new, RegisterType.BOTH);
        VendingBlock.init();
    }
}
