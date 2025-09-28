package com.furglitch.vendingblock.gui.trade;

import com.furglitch.vendingblock.VendingBlock;
import com.furglitch.vendingblock.gui.components.CustomText;
import com.furglitch.vendingblock.gui.components.SimpleEditBox;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VendorBlockScreen extends AbstractContainerScreen<VendorBlockMenu> {
    
    private static final ResourceLocation BACKGROUND =  ResourceLocation.fromNamespaceAndPath(VendingBlock.MOD_ID, "textures/gui/container/vendor_trade.png");
    private boolean showSettingsOverlay = false;
    private SimpleEditBox ownerField;
    private int tradeQuantity = -1;
    private final int MIN_QUANTITY = -1;
    private final int MAX_QUANTITY = 99;

    public VendorBlockScreen(VendorBlockMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    public void init() {
        super.init();
        ownerField();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight, 384, 256);

        graphics.blit(BACKGROUND, x + 116, y + 17, 0, 176, 16, 16, 384, 256); // Product Icon
        graphics.blit(BACKGROUND, x + 116, y + 53, 16, 176, 16, 16, 384, 256); // Price Icon
        graphics.blit(BACKGROUND, x + 176, y + 8, 0, 224, 23, 22, 384, 256); // Settings Tab
        
        // Settings Tab Icon (with Hover)
        int iconX = x + 176;
        int iconY = y + 8 + 3;
        int iconV = (mouseX >= iconX && mouseX < iconX + 16 && mouseY >= iconY && mouseY < iconY + 16) ? 16 : 0;
        graphics.blit(BACKGROUND, iconX, iconY, iconV, 192, 16, 16, 384, 256);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int bx = x + 176;
        int by = y + 8;

        // Settings Tab
        if (mouseX >= bx && mouseX < bx + 16 && mouseY >= by && mouseY < by + 16) {

            if (this.showSettingsOverlay) { this.showSettingsOverlay = false; }
            else { this.showSettingsOverlay = true; }
        }

        // Owner Text Field
        if (this.showSettingsOverlay && this.ownerField != null) {
            if (isCursorInOwnerField(mouseX, mouseY)) {
                this.ownerField.setFocused(true);
                return true;
            }
            if (this.ownerField.mouseClicked(mouseX, mouseY, button)) return true;
        }

        // Trade Quantity Buttons
        if (this.showSettingsOverlay) {
            x = (this.width - this.imageWidth) / 2;
            y = (this.height - this.imageHeight) / 2;
            int iconX = x + 176;
            int iconY = y + 8;
            int overlayWidth = 84;
            int overlayX = Math.max(x, iconX);
            int overlayY = Math.max(y, iconY);
            int qtyY = overlayY + 92;
            int minusX = overlayX + 12;
            int plusX = overlayX + overlayWidth - 14;
            int hitHalf = 8; // clickable square half-size (bigger to match button background)
            if (mouseX >= minusX - hitHalf && mouseX < minusX + hitHalf && mouseY >= qtyY - hitHalf && mouseY < qtyY + hitHalf) {
                this.tradeQuantity = Math.max(MIN_QUANTITY, this.tradeQuantity - 1);
                return true;
            }
            if (mouseX >= plusX - hitHalf && mouseX < plusX + hitHalf && mouseY >= qtyY - hitHalf && mouseY < qtyY + hitHalf) {
                this.tradeQuantity = Math.min(MAX_QUANTITY, this.tradeQuantity + 1);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (this.showSettingsOverlay) settingsOverlay(graphics, mouseX, mouseY);
        if (this.showSettingsOverlay && this.ownerField != null) {
            this.ownerField.render(graphics, mouseX, mouseY, partialTicks);
        }
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.showSettingsOverlay && this.ownerField != null && this.ownerField.isFocused() && this.ownerField.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.showSettingsOverlay && this.ownerField != null && this.ownerField.isFocused() && this.ownerField.charTyped(codePoint, modifiers)) return true;
        return super.charTyped(codePoint, modifiers);
    }

    public void settingsOverlay(GuiGraphics graphics, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int iconX = x + 176;
        int iconY = y + 8;
        int overlayWidth = 84;
        int overlayHeight = 153;
        int overlayX = Math.max(x, iconX);
        int overlayY = Math.max(y, iconY);

        graphics.blit(BACKGROUND, overlayX, overlayY, 176, 0, overlayWidth, overlayHeight, 384, 256); // Settings Background
        int iconV = (mouseX >= iconX && mouseX < iconX + 16 && mouseY >= iconY + 3 && mouseY < iconY + 3 + 16) ? 16 : 0;
        graphics.blit(BACKGROUND, iconX, iconY + 3, iconV, 192, 16, 16, 384, 256); // Settings Tab Icon (with Hover)

        graphics.blit(BACKGROUND, overlayX + 3, overlayY + 21, 32, 176, 16, 16, 384, 256); // Facade Slot Icon
        CustomText.drawLeft(graphics, this.font, Component.literal("Facade"), overlayX + 22, overlayY + 25, 1.0f, 0xFFFFFF);

        // Trade Limit Scroller
        CustomText.drawCenter(graphics, this.font, Component.literal("Trade Limit"), overlayX + (overlayWidth / 2), overlayY + 43, 1.0f, 0xFFFFFF); 
        graphics.blit(BACKGROUND, overlayX + 3, overlayY + 54, 0, 208, 16, 16, 384, 256); 
        CustomText.drawCenter(graphics, this.font, Component.literal("-"), overlayX + 11, overlayY + 58, 1.0f, 0xFFFFFF); 
        CustomText.drawCenter(graphics, this.font, Component.literal(Integer.toString(this.tradeQuantity)), overlayX + (overlayWidth / 2), overlayY + 58, 1.0f, 0xFFFFFF); 
        graphics.blit(BACKGROUND, overlayX + 64, overlayY + 54, 0, 208, 16, 16, 384, 256); 
        CustomText.drawCenter(graphics, this.font, Component.literal("+"), overlayX + 72, overlayY + 58, 1.0f, 0xFFFFFF); 

        // Admin Settings Section
        CustomText.drawCenter(graphics, this.font, Component.literal("ADMIN"), overlayX + (overlayWidth / 2), overlayY + 77, 1.0f, 0xFFFFFF);

        graphics.blit(BACKGROUND, overlayX + 3, overlayY + 89, 0, 208, 16, 16, 384, 256); // Infinite Inventory Checkbox
        CustomText.drawScroll(graphics, this.font, Component.literal("Infinite Inventory"), overlayX + 22, overlayY + 93, 1.0f, 0xFFFFFF, 55, 5);

        graphics.blit(BACKGROUND, overlayX + 3, overlayY + 109, 0, 208, 16, 16, 384, 256); // Destroy Payment Checkbox
        CustomText.drawScroll(graphics, this.font, Component.literal("Destroy Payment"), overlayX + 22, overlayY + 113, 1.0f, 0xFFFFFF, 55, 5);


    }

    public void ownerField() {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int iconX = x + 176;
        int iconY = y + 8;
        int overlayX = Math.max(x, iconX);
        int overlayY = Math.max(y, iconY);
        this.ownerField = new SimpleEditBox(this.font, overlayX + 3, overlayY + 131, 77, 16, Component.literal(""));
    }

    private boolean isCursorInOwnerField(double mouseX, double mouseY) {
        if (this.ownerField == null) return false;
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int iconX = x + 176;
        int iconY = y + 8;
        int overlayX = Math.max(x, iconX);
        int overlayY = Math.max(y, iconY);
        int fx = overlayX + 3;
        int fy = overlayY + 131;
        int fw = 77;
        int fh = 16;
        return mouseX >= fx && mouseX < fx + fw && mouseY >= fy && mouseY < fy + fh;
    }
    
}