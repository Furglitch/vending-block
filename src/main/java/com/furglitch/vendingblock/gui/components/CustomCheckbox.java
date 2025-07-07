package com.furglitch.vendingblock.gui.components;

import com.furglitch.vendingblock.VendingBlock;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CustomCheckbox extends AbstractWidget {
    
    private static final ResourceLocation CHECKBOX_UNCHECKED = ResourceLocation.fromNamespaceAndPath(VendingBlock.MODID, "textures/gui/vendingblock/checkbox.png");
    private static final ResourceLocation CHECKBOX_CHECKED = ResourceLocation.fromNamespaceAndPath(VendingBlock.MODID, "textures/gui/vendingblock/checkbox_on.png");
    public static int checkboxSize = 6;
    
    private boolean selected;
    
    public CustomCheckbox(int x, int y, int width, int height, Component message, boolean selected) {
        super(x, y, width, height, message);
        this.selected = selected;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        
        ResourceLocation texture = this.selected ? CHECKBOX_CHECKED : CHECKBOX_UNCHECKED;
        RenderSystem.setShaderTexture(0, texture);
        guiGraphics.blit(texture, this.getX(), this.getY(), 0, 0, checkboxSize, checkboxSize, checkboxSize, checkboxSize);
        
        if (this.getMessage() != null) {
            int textColor = this.active ? 0xFFFFFF : 0xA0A0A0;
            guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX() + checkboxSize + 4, this.getY() + (checkboxSize - 8) / 2, textColor);
        }
    }
    
    @Override
    public void onClick(double mouseX, double mouseY) {
        this.selected = !this.selected;
        this.playDownSound(Minecraft.getInstance().getSoundManager());
    }
    
    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(net.minecraft.client.gui.narration.NarratedElementType.TITLE, 
            Component.translatable("gui.narrate.checkbox", this.getMessage(), 
                this.selected ? Component.translatable("gui.yes") : Component.translatable("gui.no")));
    }
    
    public boolean selected() {
        return this.selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public static CustomCheckboxBuilder customBuilder(Component message, Font font) {
        return new CustomCheckboxBuilder(message, font);
    }
    
    public static class CustomCheckboxBuilder {
        private final Component message;
        private int x = 0;
        private int y = 0;
        private int width = 10;
        private int height = 10;
        private boolean selected = false;
        
        public CustomCheckboxBuilder(Component message, Font font) {
            this.message = message;
            this.width = 10 + 4 + font.width(message);
        }
        
        public CustomCheckboxBuilder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }
        
        public CustomCheckboxBuilder selected(boolean selected) {
            this.selected = selected;
            return this;
        }
        
        public CustomCheckbox build() {
            return new CustomCheckbox(x, y, width, height, message, selected);
        }
    }
}
