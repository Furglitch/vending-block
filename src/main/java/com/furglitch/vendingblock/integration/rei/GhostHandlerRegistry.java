package com.furglitch.vendingblock.integration.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public class GhostHandlerRegistry implements REIClientPlugin {

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerDraggableStackVisitor(new GhostHandlerBlock());
        registry.registerDraggableStackVisitor(new GhostHandlerAdmin());
    }
}
