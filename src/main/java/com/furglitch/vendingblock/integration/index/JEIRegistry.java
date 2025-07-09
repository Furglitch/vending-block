package com.furglitch.vendingblock.integration.index;

import com.furglitch.vendingblock.gui.admin.VendorAdminScreen;
import com.furglitch.vendingblock.gui.trade.VendorBlockScreen;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIRegistry implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("vendingblock","jei_plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGhostIngredientHandler(VendorBlockScreen.class, new MenuGhostHandler());
        registration.addGhostIngredientHandler(VendorAdminScreen.class, new AdminGhostHandler());
    }
    
}
