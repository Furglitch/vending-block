package com.furglitch.vendingblock.integration.jei;

import com.furglitch.vendingblock.gui.admin.VendorAdminScreen;
import com.furglitch.vendingblock.gui.trade.VendorBlockScreen;
import com.furglitch.vendingblock.gui.display.DisplayBlockScreen;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class GhostHandlerRegistry implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("vendingblock","jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(VendorBlockScreen.class, new GhostHandlerBlock());
        registration.addGhostIngredientHandler(VendorAdminScreen.class, new GhostHandlerAdmin());
        registration.addGhostIngredientHandler(DisplayBlockScreen.class, new GhostHandlerDisplay());
    }
    
}
