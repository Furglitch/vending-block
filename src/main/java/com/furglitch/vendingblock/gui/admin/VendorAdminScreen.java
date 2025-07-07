package com.furglitch.vendingblock.gui.admin;

import com.furglitch.vendingblock.VendingBlock;
import com.furglitch.vendingblock.network.OwnerChangePacket;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class VendorAdminScreen extends AbstractContainerScreen<VendorAdminMenu> {

    private static final ResourceLocation TEXTURE =  ResourceLocation.fromNamespaceAndPath(VendingBlock.MODID, "textures/gui/vendingblock/admin.png");
    private EditBox ownerField;
    private String initialOwnerValue;

    public VendorAdminScreen(VendorAdminMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        ownerField = new EditBox(this.font, x + 43, y + 16, 90, 18, Component.literal("Owner"));
        ownerField.setMaxLength(32);
        ownerField.setBordered(true);
        ownerField.setVisible(true);
        ownerField.setTextColor(0xFFFFFF);
        ownerField.setCanLoseFocus(true);
        ownerField.setFocused(false);
        
        String currentOwner = menu.blockEntity.getOwnerUser();
        if (currentOwner != null) {
            ownerField.setValue(currentOwner);
            initialOwnerValue = currentOwner;
        } else {
            initialOwnerValue = "";
        }
        
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
        if (this.getFocused() == ownerField) {
            if (pKeyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER || 
                pKeyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER) {
                sendOwnerChangeIfChanged();
                return true;
            }
            
            if (pKeyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
                ownerField.setFocused(false);
                this.setFocused(null);
                return super.keyPressed(pKeyCode, pScanCode, pModifiers);
            }
            
            if (ownerField.keyPressed(pKeyCode, pScanCode, pModifiers)) {
                return true;
            }
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (this.getFocused() == ownerField) {
            return ownerField.charTyped(pCodePoint, pModifiers);
        }
        return super.charTyped(pCodePoint, pModifiers);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (ownerField.mouseClicked(pMouseX, pMouseY, pButton)) {
            this.setFocused(ownerField);
            return true;
        } else {
            if (this.getFocused() == ownerField) {
                ownerField.setFocused(false);
                this.setFocused(null);
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public void setFocused(net.minecraft.client.gui.components.events.GuiEventListener pListener) {
        super.setFocused(pListener);
        if (pListener == ownerField) {
            ownerField.setFocused(true);
        } else if (ownerField != null) {
            ownerField.setFocused(false);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        sendOwnerChangeIfChanged();
        super.onClose();
    }

    private void sendOwnerChangeIfChanged() {
        String newOwner = ownerField.getValue().trim();
        if (!newOwner.equals(initialOwnerValue)) {
            OwnerChangePacket packet = new OwnerChangePacket(menu.blockEntity.getBlockPos(), newOwner);
            PacketDistributor.sendToServer(packet);
        }
    }

    
}
