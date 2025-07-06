package com.furglitch.vendingblock;

import org.slf4j.Logger;

import com.furglitch.vendingblock.gui.hud.HintOverlay;
import com.furglitch.vendingblock.gui.trade.VendorBlockScreen;
import com.furglitch.vendingblock.registry.BlockEntityRegistry;
import com.furglitch.vendingblock.registry.BlockRegistry;
import com.furglitch.vendingblock.registry.ItemRegistry;
import com.furglitch.vendingblock.registry.MenuRegistry;
import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(VendingBlock.MODID)
public class VendingBlock {

    public static final String MODID = "vendingblock";
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public VendingBlock(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
        ItemRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);
        BlockEntityRegistry.register(modEventBus);
        MenuRegistry.register(modEventBus);
        //TabRegistry.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(BlockRegistry.VENDOR);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @SuppressWarnings("removal")
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                NeoForge.EVENT_BUS.register(HintOverlay.class);
            });
        }

        @SubscribeEvent
        public static void registerMenus(RegisterMenuScreensEvent event) {
            event.register(MenuRegistry.VENDOR_MENU.get(), VendorBlockScreen::new);
        }

    }

}