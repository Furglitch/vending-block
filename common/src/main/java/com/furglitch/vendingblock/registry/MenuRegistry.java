package com.furglitch.vendingblock.registry;

import java.util.function.Supplier;

import com.furglitch.vendingblock.VendingBlock;
import com.furglitch.vendingblock.gui.trade.VendorBlockMenu;
import com.furglitch.vendingblock.gui.trade.VendorBlockScreen;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;

public class MenuRegistry {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(VendingBlock.MOD_ID, Registries.MENU); 

    public static RegistrySupplier<MenuType<VendorBlockMenu>> VENDOR_MENU = MENUS.register(
        "vendor_menu",
        () -> new MenuType<VendorBlockMenu>(
            VendorBlockMenu::new,
            defaultProperties("vendor_menu")
        )
    );

    public static void init() {
        MENUS.register();
        ClientLifecycleEvent.CLIENT_STARTED.register(client -> {
            dev.architectury.registry.menu.MenuRegistry.registerScreenFactory(VENDOR_MENU.get(), VendorBlockScreen::new);
        });
    }

    public static <T extends MenuType<?>> RegistrySupplier<?> register(String name, Supplier<T> menu) {
        return MENUS.register(
            ResourceLocation.fromNamespaceAndPath(VendingBlock.MOD_ID, name),
            menu
        );
    }

    public static FeatureFlagSet defaultProperties(String name) {
        return FeatureFlagSet.of();
    }

}
