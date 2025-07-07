package com.furglitch.vendingblock;

import org.slf4j.Logger;

import com.furglitch.vendingblock.blockentity.VendorBlockEntityDisplay;
import com.furglitch.vendingblock.gui.admin.VendorAdminScreen;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
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
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(com.furglitch.vendingblock.network.NetworkHandler::register);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(BlockRegistry.VENDOR);
            event.accept(ItemRegistry.VENDOR_KEY);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            BlockEntityRegistry.VENDOR_BE.get(),
            (vendorBlockEntity, side) -> {
                if (side == null) {
                    return vendorBlockEntity.getPublicItemHandler();
                } else {
                    return vendorBlockEntity.getInsertItemHandler();
                }
            }
        );
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
            event.register(MenuRegistry.VENDOR_ADMIN_MENU.get(), VendorAdminScreen::new);
        }

        @SubscribeEvent
        public static void registerDisplay(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(BlockEntityRegistry.VENDOR_BE.get(), VendorBlockEntityDisplay::new);
        }

    }

}