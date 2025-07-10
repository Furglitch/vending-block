package com.furglitch.vendingblock.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CustomCheckbox extends AbstractWidget {
    private static final ResourceLocation CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE = 
        ResourceLocation.withDefaultNamespace("widget/checkbox_selected_highlighted");
    private static final ResourceLocation CHECKBOX_SELECTED_SPRITE = 
        ResourceLocation.withDefaultNamespace("widget/checkbox_selected");
    private static final ResourceLocation CHECKBOX_HIGHLIGHTED_SPRITE = 
        ResourceLocation.withDefaultNamespace("widget/checkbox_highlighted");
    private static final ResourceLocation CHECKBOX_SPRITE = 
        ResourceLocation.withDefaultNamespace("widget/checkbox");

    private boolean selected;
    private final int checkboxSize;

    public CustomCheckbox(int x, int y, int width, int height, Component message, Font font, boolean selected) {
        super(x, y, width, height, message);
        this.selected = selected;
        this.checkboxSize = Math.min(width, height);
    }

    public static Builder builder(Component message, Font font) {
        return new Builder(message, font);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.selected = !this.selected;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation sprite;
        if (this.selected) {
            sprite = this.isFocused() ? CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE : CHECKBOX_SELECTED_SPRITE;
        } else {
            sprite = this.isFocused() ? CHECKBOX_HIGHLIGHTED_SPRITE : CHECKBOX_SPRITE;
        }

        guiGraphics.blitSprite(sprite, this.getX(), this.getY(), this.checkboxSize, this.checkboxSize);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(net.minecraft.client.gui.narration.NarratedElementType.TITLE, 
            Component.translatable("narration.checkbox", this.getMessage()));
        if (this.active) {
            if (this.isFocused()) {
                narrationElementOutput.add(net.minecraft.client.gui.narration.NarratedElementType.USAGE, 
                    Component.translatable("narration.checkbox.usage.focused"));
            } else {
                narrationElementOutput.add(net.minecraft.client.gui.narration.NarratedElementType.USAGE, 
                    Component.translatable("narration.checkbox.usage.hovered"));
            }
        }
    }

    public boolean selected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public static class Builder {
        private final Component message;
        private final Font font;
        private int x = 0;
        private int y = 0;
        private int width = 20;
        private int height = 20;
        private boolean selected = false;

        public Builder(Component message, Font font) {
            this.message = message;
            this.font = font;
        }

        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public CustomCheckbox build() {
            return new CustomCheckbox(this.x, this.y, this.width, this.height, this.message, this.font, this.selected);
        }
    }
}
