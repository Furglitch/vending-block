package com.furglitch.vendingblock.integration.emi;

import com.furglitch.vendingblock.gui.admin.VendorAdminScreen;
import com.furglitch.vendingblock.gui.trade.VendorBlockScreen;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class GhostHandlerRegistry implements EmiPlugin {
    
    @Override
    public void register(EmiRegistry registry) {
        registry.addDragDropHandler(VendorBlockScreen.class, new GhostHandlerBlock());
        registry.addDragDropHandler(VendorAdminScreen.class, new GhostHandlerAdmin());
    }
    
}
