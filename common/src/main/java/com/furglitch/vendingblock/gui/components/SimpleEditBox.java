package com.furglitch.vendingblock.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * Small wrapper around Minecraft's EditBox that exposes a render helper
 * using GuiGraphics and small helpers for forwarding input.
 */
public class SimpleEditBox {

    private final EditBox editBox;

    public SimpleEditBox(Font font, int x, int y, int width, int height, Component narrative) {
        this.editBox = new EditBox(font, x, y, width, height, narrative);
    }

    public void setValue(String s) { this.editBox.setValue(s); }
    public String getValue() { return this.editBox.getValue(); }

    public void setFocused(boolean focused) { this.editBox.setFocused(focused); }
    public boolean isFocused() { return this.editBox.isFocused(); }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.editBox.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char codePoint, int modifiers) {
        return this.editBox.charTyped(codePoint, modifiers);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.editBox.mouseClicked(mouseX, mouseY, button);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.editBox.render(graphics, mouseX, mouseY, partialTicks);
    }

    public EditBox getWidget() { return this.editBox; }

}
