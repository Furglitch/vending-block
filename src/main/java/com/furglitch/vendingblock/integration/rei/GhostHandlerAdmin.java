package com.furglitch.vendingblock.integration.rei;

import java.util.stream.Stream;

import com.furglitch.vendingblock.gui.admin.VendorAdminScreen;
import com.furglitch.vendingblock.gui.components.FilterSlot;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.drag.DraggableStack;
import me.shedaniel.rei.api.client.gui.drag.DraggableStackVisitor;
import me.shedaniel.rei.api.client.gui.drag.DraggedAcceptorResult;
import me.shedaniel.rei.api.client.gui.drag.DraggingContext;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

public class GhostHandlerAdmin implements DraggableStackVisitor<VendorAdminScreen> {

    @Override
    public <R extends Screen> boolean isHandingScreen(R screen) {
        return screen instanceof VendorAdminScreen;
    }

    @Override
    public DraggedAcceptorResult acceptDraggedStack(DraggingContext<VendorAdminScreen> context, DraggableStack stack) {
        Object stackObject = stack.getStack().getValue();
        if (!(stackObject instanceof ItemStack itemStack)) {
            return DraggedAcceptorResult.PASS;
        }

        if (itemStack.isEmpty()) {
            return DraggedAcceptorResult.PASS;
        }

        VendorAdminScreen screen = context.getScreen();
        int mouseX = context.getCurrentPosition().getX();
        int mouseY = context.getCurrentPosition().getY();

        for (FilterSlot slot : screen.getFilterSlots()) {
            int slotX = screen.getGuiLeft() + slot.x;
            int slotY = screen.getGuiTop() + slot.y;

            if (mouseX >= slotX && mouseX < slotX + 16 && mouseY >= slotY && mouseY < slotY + 16) {
                slot.set(itemStack);
                return DraggedAcceptorResult.ACCEPTED;
            }
        }

        return DraggedAcceptorResult.PASS;
    }

    @Override
    public Stream<BoundsProvider> getDraggableAcceptingBounds(DraggingContext<VendorAdminScreen> context, DraggableStack stack) {
        VendorAdminScreen screen = context.getScreen();
        return screen.getFilterSlots().stream().map(slot -> BoundsProvider.ofRectangle(new Rectangle(screen.getGuiLeft() + slot.x, screen.getGuiTop() + slot.y, 16, 16)));
    }
}
