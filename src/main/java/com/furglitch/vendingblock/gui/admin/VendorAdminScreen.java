package com.furglitch.vendingblock.gui.admin;

import com.furglitch.vendingblock.VendingBlock;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VendorAdminScreen extends AbstractContainerScreen<VendorAdminMenu> {

    private static final ResourceLocation TEXTURE =  ResourceLocation.fromNamespaceAndPath(VendingBlock.MODID, "textures/gui/vendingblock/admin.png");
    private EditBox ownerField;

    public VendorAdminScreen(VendorAdminMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        ownerField = new EditBox(this.font, x + 43, y + 16, 89, 17, Component.literal("Owner"));
        ownerField.setMaxLength(32);
        ownerField.setBordered(true);
        ownerField.setVisible(true);
        ownerField.setTextColor(0xFFFFFF);
        this.addRenderableWidget(ownerField);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, 176, 166);
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (ownerField.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (ownerField.charTyped(pCodePoint, pModifiers)) {
            return true;
        }
        return super.charTyped(pCodePoint, pModifiers);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (ownerField.mouseClicked(pMouseX, pMouseY, pButton)) {
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    
}
