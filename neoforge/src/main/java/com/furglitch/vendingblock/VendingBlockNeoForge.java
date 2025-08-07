package com.furglitch.vendingblock;

import com.furglitch.vendingblock.config.ClientConfig;
import com.furglitch.vendingblock.config.ServerConfig;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.neoforged.fml.common.Mod;

@Mod(VendingBlock.MOD_ID)
public final class VendingBlockNeoForge {

    public VendingBlockNeoForge() {
        ConfigApiJava.registerAndLoadConfig(ClientConfig::new, RegisterType.CLIENT);
        ConfigApiJava.registerAndLoadConfig(ServerConfig::new, RegisterType.BOTH);

        VendingBlock.init();
    }
}
