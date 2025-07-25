package com.furglitch.vendingblock.integration.rei;

import java.util.stream.Stream;

import com.furglitch.vendingblock.gui.display.DisplayBlockScreen;
import com.furglitch.vendingblock.gui.components.FilterSlot;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.drag.DraggableStack;
import me.shedaniel.rei.api.client.gui.drag.DraggableStackVisitor;
import me.shedaniel.rei.api.client.gui.drag.DraggedAcceptorResult;
import me.shedaniel.rei.api.client.gui.drag.DraggingContext;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

public class GhostHandlerDisplay implements DraggableStackVisitor<DisplayBlockScreen> {

    @Override
    public <R extends Screen> boolean isHandingScreen(R screen) {
        return screen instanceof DisplayBlockScreen;
    }

    @Override
    public DraggedAcceptorResult acceptDraggedStack(DraggingContext<DisplayBlockScreen> context, DraggableStack stack) {
        Object stackObject = stack.getStack().getValue();
        if (!(stackObject instanceof ItemStack itemStack)) {
            return DraggedAcceptorResult.PASS;
        }

        if (itemStack.isEmpty()) {
            return DraggedAcceptorResult.PASS;
        }

        DisplayBlockScreen screen = context.getScreen();
        int mouseX = context.getCurrentPosition().getX();
        int mouseY = context.getCurrentPosition().getY();

        for (FilterSlot slot : screen.getFilterSlots().stream().filter(s -> s.getSlotIndex() != 0).toList()) {
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
    public Stream<BoundsProvider> getDraggableAcceptingBounds(DraggingContext<DisplayBlockScreen> context, DraggableStack stack) {
        DisplayBlockScreen screen = context.getScreen();
        return screen.getFilterSlots().stream().filter(s -> s.getSlotIndex() != 0).toList().stream().map(slot -> BoundsProvider.ofRectangle(new Rectangle(screen.getGuiLeft() + slot.x, screen.getGuiTop() + slot.y, 16, 16)));
    }
}
