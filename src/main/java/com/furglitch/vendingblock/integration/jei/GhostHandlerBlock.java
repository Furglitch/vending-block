package com.furglitch.vendingblock.integration.jei;

import java.util.ArrayList;
import java.util.List;

import com.furglitch.vendingblock.gui.components.FilterSlot;
import com.furglitch.vendingblock.gui.trade.VendorBlockScreen;

import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;

public class GhostHandlerBlock implements IGhostIngredientHandler<VendorBlockScreen>  {

    @Override
    public <I> List<Target<I>> getTargetsTyped(VendorBlockScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
        List<Target<I>> targets = new ArrayList<>();
        if (ingredient.getIngredient() instanceof ItemStack) {
            for (FilterSlot slot : screen.getFilterSlots().stream().filter(s -> s.getSlotIndex() != 0).toList()) {
                targets.add(new Target<>() {
                    @Override public Rect2i getArea() { return new Rect2i(screen.getGuiLeft() + slot.x, screen.getGuiTop() + slot.y, 16, 16); }
                    @Override public void accept(I ingredient) { slot.set((ItemStack) ingredient); }
                });
            }
        }
        return targets;
    }

    @Override public void onComplete() {}
    
}
