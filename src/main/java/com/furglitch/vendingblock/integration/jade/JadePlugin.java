package com.furglitch.vendingblock.integration.jade;

import com.furglitch.vendingblock.blockentity.VendorBlockEntity;
import com.furglitch.vendingblock.registry.BlockRegistry;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(VendorBlockDataProvider.INSTANCE, VendorBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(VendorBlockComponentProvider.INSTANCE, BlockRegistry.VENDOR.get().getClass());
    }
    
}
