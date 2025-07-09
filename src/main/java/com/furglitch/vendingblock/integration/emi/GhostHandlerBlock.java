package com.furglitch.vendingblock.integration.emi;

import com.furglitch.vendingblock.gui.components.FilterSlot;
import com.furglitch.vendingblock.gui.trade.VendorBlockScreen;

import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.runtime.EmiDrawContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class GhostHandlerBlock implements EmiDragDropHandler<VendorBlockScreen> {

    @Override
    public boolean dropStack(VendorBlockScreen screen, EmiIngredient stack, int x, int y) {
        if (!(stack instanceof EmiStack emiStack)) {
            return false;
        }
        
        ItemStack itemStack = emiStack.getItemStack();
        if (itemStack.isEmpty()) {
            return false;
        }
        
        for (FilterSlot slot : screen.getFilterSlots().stream().filter(s -> s.getSlotIndex() != 0).toList()) {
            int slotX = screen.getGuiLeft() + slot.x;
            int slotY = screen.getGuiTop() + slot.y;
            
            if (x >= slotX && x < slotX + 16 && y >= slotY && y < slotY + 16) {
                slot.set(itemStack);
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void render(VendorBlockScreen screen, EmiIngredient dragged, GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (!(dragged instanceof EmiStack emiStack)) {
            return;
        }
        
        ItemStack itemStack = emiStack.getItemStack();
        if (itemStack.isEmpty()) {
            return;
        }
        
        EmiDrawContext context = EmiDrawContext.wrap(guiGraphics);
        
        for (FilterSlot slot : screen.getFilterSlots().stream().filter(s -> s.getSlotIndex() != 0).toList()) {
            int slotX = screen.getGuiLeft() + slot.x;
            int slotY = screen.getGuiTop() + slot.y;
            
            context.fill(slotX, slotY, 16, 16, 0x8800FF00);
        }
    }
    
}
