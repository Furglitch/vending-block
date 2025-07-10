package com.furglitch.vendingblock.gui.admin;


import java.util.ArrayList;
import java.util.List;

import com.furglitch.vendingblock.VendingBlock;
import com.furglitch.vendingblock.gui.components.CustomCheckbox;
import com.furglitch.vendingblock.gui.components.FilterSlot;
import com.furglitch.vendingblock.network.InfiniteInventoryPacket;
import com.furglitch.vendingblock.network.OwnerChangePacket;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;

public class VendorAdminScreen extends AbstractContainerScreen<VendorAdminMenu> {

    private static final ResourceLocation BACKGROUND =  ResourceLocation.fromNamespaceAndPath(VendingBlock.MODID, "textures/gui/container/vendor_admin.png");
    private static final ResourceLocation ARROW =  ResourceLocation.fromNamespaceAndPath(VendingBlock.MODID, "textures/gui/container/slot/arrow.png");
    private static final ResourceLocation FACADE =  ResourceLocation.fromNamespaceAndPath(VendingBlock.MODID, "textures/gui/container/slot/facade.png");

    private EditBox ownerField;
    private String initialOwnerValue;
    private CustomCheckbox infiniteCheckbox;
    private CustomCheckbox discardCheckbox;
    private boolean infiniteInit;
    private boolean discardInit;

    public VendorAdminScreen(VendorAdminMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        ownerField = new EditBox(this.font, x + 25, y + 16, 90, 18, Component.translatable("menu.vendingblock.admin.owner"));
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
        
        boolean infiniteStatus = menu.blockEntity.isInfinite();
        infiniteCheckbox = CustomCheckbox.builder(Component.literal(""), this.font)
            .pos(x + 115, y + 16)
            .size(18, 18)
            .selected(infiniteStatus)
            .build();
        infiniteCheckbox.setTooltip(net.minecraft.client.gui.components.Tooltip.create(
            Component.translatable("menu.vendingblock.tooltip.infinite")));
        infiniteInit = infiniteStatus;
        this.addRenderableWidget(infiniteCheckbox);

        boolean discardStatus = menu.blockEntity.isDiscarding();
        discardCheckbox = CustomCheckbox.builder(Component.literal(""), this.font)
            .pos(x + 133, y + 16)
            .size(18, 18)
            .selected(discardStatus)
            .build();
        discardCheckbox.setTooltip(net.minecraft.client.gui.components.Tooltip.create(
            Component.translatable("menu.vendingblock.tooltip.discard")));
        discardInit = discardStatus;
        this.addRenderableWidget(discardCheckbox);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(BACKGROUND, x, y, 0, 0, 176, 166);

        List<FilterSlot> filterSlots = getFilterSlots();
        if (isSlotEmpty(0, filterSlots)) guiGraphics.blit(ARROW, x + 62, y + 53, 0, 0, 16, 16, 16, 16);

        if (isSlotEmpty(10, filterSlots)) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(x + 80 + 8, y + 53 + 8, 0);
            guiGraphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180.0F));
            guiGraphics.blit(ARROW, -8, -8, 0, 0, 16, 16, 16, 16);
            guiGraphics.pose().popPose();
        }

        if (isSlotEmpty(11, filterSlots)) guiGraphics.blit(FACADE, x + 98, y + 53, 0, 0, 16, 16, 16, 16);
    }
    
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        
        this.renderFilterSlotTooltips(pGuiGraphics, pMouseX, pMouseY);
    }
    
    private void renderFilterSlotTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        
        List<FilterSlot> filterSlots = getFilterSlots();
        
        if (isMouseOverSlot(mouseX, mouseY, x + 62, y + 53) && isSlotEmpty(0, filterSlots)) {
            Component tooltip = Component.translatable("menu.vendingblock.tooltip.product");
            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
        else if (isMouseOverSlot(mouseX, mouseY, x + 80, y + 53) && isSlotEmpty(10, filterSlots)) {
            Component tooltip = Component.translatable("menu.vendingblock.tooltip.price");
            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
        else if (isMouseOverSlot(mouseX, mouseY, x + 98, y + 53) && isSlotEmpty(11, filterSlots)) {
            Component tooltip = Component.translatable("menu.vendingblock.tooltip.facade");
            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }
    
    private boolean isSlotEmpty(int slotIndex, List<FilterSlot> filterSlots) {
        for (FilterSlot slot : filterSlots) {
            if (slot.getSlotIndex() == slotIndex) {
                return !slot.hasItem();
            }
        }
        return true;
    }
    
    private boolean isMouseOverSlot(int mouseX, int mouseY, int slotX, int slotY) {
        return mouseX >= slotX && mouseX < slotX + 16 && mouseY >= slotY && mouseY < slotY + 16;
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
        } else if (infiniteCheckbox.mouseClicked(pMouseX, pMouseY, pButton)) {
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
        sendInfiniteInventoryChangeIfChanged();
        sendDiscardsPaymentChangeIfChanged();
        super.onClose();
    }

    private void sendOwnerChangeIfChanged() {
        String newOwner = ownerField.getValue().trim();
        if (!newOwner.equals(initialOwnerValue)) {
            OwnerChangePacket packet = new OwnerChangePacket(menu.blockEntity.getBlockPos(), newOwner);
            PacketDistributor.sendToServer(packet);
        }
    }

    private void sendInfiniteInventoryChangeIfChanged() {
        boolean newInfiniteInventory = infiniteCheckbox.selected();
        if (newInfiniteInventory != infiniteInit) {
            InfiniteInventoryPacket packet = new InfiniteInventoryPacket(menu.blockEntity.getBlockPos(), newInfiniteInventory);
            PacketDistributor.sendToServer(packet);
        }
    }

    private void sendDiscardsPaymentChangeIfChanged() {
        boolean newDiscardsPayment = discardCheckbox.selected();
        if (newDiscardsPayment != discardInit) {
            com.furglitch.vendingblock.network.DiscardsPaymentPacket packet = new com.furglitch.vendingblock.network.DiscardsPaymentPacket(menu.blockEntity.getBlockPos(), newDiscardsPayment);
            PacketDistributor.sendToServer(packet);
        }
    }

    public List<FilterSlot> getFilterSlots() {
        List<FilterSlot> filterSlots = new ArrayList<>();
        for (Slot slot : this.menu.slots) {
            if (slot instanceof FilterSlot filterSlot) {
                filterSlots.add(filterSlot);
            }
        }
        return filterSlots;
    }

    
}
