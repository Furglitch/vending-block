package com.furglitch.vendingblock.registry;

import com.furglitch.vendingblock.VendingBlock;
import com.furglitch.vendingblock.gui.admin.VendorAdminMenu;
import com.furglitch.vendingblock.gui.display.DisplayBlockMenu;
import com.furglitch.vendingblock.gui.trade.VendorBlockMenu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MenuRegistry {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, VendingBlock.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<VendorBlockMenu>> VENDOR_MENU = registerMenuType("vendor_menu", VendorBlockMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<VendorAdminMenu>> VENDOR_ADMIN_MENU = registerMenuType("vendor_admin_menu", VendorAdminMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<DisplayBlockMenu>> DISPLAY_MENU = registerMenuType("display_menu", DisplayBlockMenu::new);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }

}
