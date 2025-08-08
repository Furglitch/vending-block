package com.furglitch.vendingblock;

import com.furglitch.vendingblock.config.ClientConfig;
import com.furglitch.vendingblock.config.ServerConfig;
import com.furglitch.vendingblock.registry.BlockRegistry;
import com.furglitch.vendingblock.registry.ItemRegistry;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;

public final class VendingBlock {
    public static final String MOD_ID = "vendingblock";

    public static void init() {
        ConfigApiJava.registerAndLoadConfig(ClientConfig::new, RegisterType.CLIENT);
        ConfigApiJava.registerAndLoadConfig(ServerConfig::new, RegisterType.BOTH);      

        BlockRegistry.init();
        ItemRegistry.init();  
    }
}
