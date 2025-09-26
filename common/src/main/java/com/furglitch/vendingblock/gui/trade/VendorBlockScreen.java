package com.furglitch.vendingblock.gui.trade;

import com.furglitch.vendingblock.VendingBlock;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VendorBlockScreen extends AbstractContainerScreen<VendorBlockMenu> {
    
    private static final ResourceLocation BACKGROUND =  ResourceLocation.fromNamespaceAndPath(VendingBlock.MOD_ID, "textures/gui/container/vendor_trade.png");
    private static final ResourceLocation ARROW =  ResourceLocation.fromNamespaceAndPath(VendingBlock.MOD_ID, "textures/gui/container/slot/arrow.png");
    private static final ResourceLocation FACADE =  ResourceLocation.fromNamespaceAndPath(VendingBlock.MOD_ID, "textures/gui/container/slot/facade.png");


    public VendorBlockScreen(VendorBlockMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(BACKGROUND, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }
    
}