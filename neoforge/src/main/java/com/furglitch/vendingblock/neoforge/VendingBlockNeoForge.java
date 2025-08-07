package com.furglitch.vendingblock.neoforge;

import com.furglitch.vendingblock.VendingBlock;
import com.furglitch.vendingblock.neoforge.config.ClientConfig;
import com.furglitch.vendingblock.neoforge.config.ServerConfig;

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
