package com.furglitch.vendingblock.neoforge;

import com.furglitch.vendingblock.VendingBlock;
import com.furglitch.vendingblock.gui.display.DisplayBlockScreen;
import com.furglitch.vendingblock.gui.trade.VendorBlockScreen;
import com.furglitch.vendingblock.registry.MenuRegistry;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(VendingBlock.MOD_ID)
public final class VendingBlockNeoForge {

    public VendingBlockNeoForge() {
        VendingBlock.init();
    }

    @EventBusSubscriber(modid = VendingBlock.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(MenuRegistry.VENDOR_MENU.get(), VendorBlockScreen::new);
            event.register(MenuRegistry.DISPLAY_MENU.get(), DisplayBlockScreen::new);
        }
    }

}
