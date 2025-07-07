package com.furglitch.vendingblock.gui.hud;

import com.furglitch.vendingblock.blockentity.VendorBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public class HintOverlay {
    
    private static final int ColorBCK = 0xC0161616;
    private static final int ColorBDR = 0xD0161616;
    private static final int ColorTXT = 0xFFFFFFFF;
    private static Component sellItemText = null, buyItemText = null;
    private static int saleType = 0;

    @SubscribeEvent
    public static void onRenderGUI(RenderGuiEvent.Post event){

        if (ModList.get().isLoaded("jade")) return;

        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null || mc.level == null) return;

        HitResult hit = mc.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) return;

        BlockHitResult blockHit = (BlockHitResult) hit;
        BlockEntity blockEntity = mc.level.getBlockEntity(blockHit.getBlockPos());
        if (! (blockEntity instanceof VendorBlockEntity entity)) return;

        renderHint(event.getGuiGraphics(), entity, mc);

    }

    private static void renderHint(GuiGraphics gui, VendorBlockEntity entity, Minecraft mc) {
        ItemStack sellItem = entity.inventory.getStackInSlot(0); // product
        ItemStack buyItem = entity.inventory.getStackInSlot(10); // price
        if (sellItem.isEmpty() && buyItem.isEmpty()) return;

        switch ((sellItem.isEmpty() ? 0 : 1) << 1 | (buyItem.isEmpty() ? 0 : 1)) {
            case 3: // trade - !sellItem.isEmpty() && !buyItem.isEmpty()
                saleType = 1;
                break;
            case 2: // giveaway - !sellItem.isEmpty() && buyItem.isEmpty()
                saleType = 2;
                break;
            case 1: // donation - sellItem.isEmpty() && !buyItem.isEmpty()
                saleType = 3;
                break;
            default:
                saleType = 0;
                break;
        }
        HintDimensions dimensions = calculateDimensions(mc, entity.getOwnerUser(), sellItem, buyItem, saleType, mc.font.lineHeight+2);

        int margin = 8;
        int w = dimensions.width + (margin * 2);
        int h = dimensions.height + (margin * 2);
        int x = (mc.getWindow().getGuiScaledWidth() - w) / 2;
        int y = 8;

        drawBackground(gui, x, y, w, h);

        renderContent(gui, mc, entity.getOwnerUser(), sellItem, buyItem, x, y + margin, w, mc.font.lineHeight+2);

    }

    private static HintDimensions calculateDimensions(Minecraft mc, String owner, ItemStack sellItem, ItemStack buyItem, int saleType, int lineHeight) {
        int maxWidth = 0;
        int totalHeight = 0;

        maxWidth = Math.max(maxWidth, mc.font.width(owner));
        totalHeight += lineHeight;
        switch (saleType) {
            case 1:
                sellItemText = Component.translatable("ui.vendingblock.sell");
                buyItemText = Component.translatable("ui.vendingblock.buy");
                maxWidth = Math.max(maxWidth, mc.font.width(sellItemText.getString()));
                maxWidth = Math.max(maxWidth, mc.font.width(buyItemText.getString()));
                maxWidth = Math.max(maxWidth, calculateItemDimensions(mc, buyItem).width);
                maxWidth = Math.max(maxWidth, calculateItemDimensions(mc, sellItem).width);
                totalHeight += lineHeight * 4;
                break;
            case 2:
                sellItemText = Component.translatable("ui.vendingblock.giveaway");
                maxWidth = Math.max(maxWidth, mc.font.width(sellItemText.getString()));
                maxWidth = Math.max(maxWidth, calculateItemDimensions(mc, sellItem).width);
                totalHeight += lineHeight * 2;
                break;
            case 3:
                buyItemText = Component.translatable("ui.vendingblock.request");
                maxWidth = Math.max(maxWidth, mc.font.width(buyItemText.getString()));
                maxWidth = Math.max(maxWidth, calculateItemDimensions(mc, buyItem).width);
                totalHeight += lineHeight * 2;
                break;
        }

        return new HintDimensions(maxWidth, totalHeight);
    }
    
    private static ItemDimensions calculateItemDimensions(Minecraft mc, ItemStack item) {
        String itemText = formatItemText(item);
        int width = 16 + 4 + mc.font.width(itemText);
        return new ItemDimensions(width);
    }
    
    private static String formatItemText(ItemStack item) {
        Component itemName = item.getHoverName();
        return item.getCount() > 1 ? 
            itemName.getString() + " x" + item.getCount() : 
            itemName.getString();
    }

    private static void drawBackground(GuiGraphics gui, int x, int y, int w, int h) {
        gui.fill(x, y, x + w, y + h, ColorBCK);
        gui.fill(x, y, x + w, y + 1, ColorBDR);
        gui.fill(x, y + h - 1, x + w, y + h, ColorBDR);
        gui.fill(x, y, x + 1, y + h, ColorBDR);
        gui.fill(x + w - 1, y, x + w, y + h, ColorBDR);
    }

    public static int renderContent(GuiGraphics gui, Minecraft mc, String owner, ItemStack sellItem, ItemStack buyItem, int x, int y, int w, int h) {
        drawText(gui, mc, owner, x, y, w, ColorTXT);
        y += h;
        
        switch (saleType) {
            case 1:
                drawText(gui, mc, sellItemText.getString(), x, y, w, ColorTXT & 0x80FFFFFF);
                y += h;
                drawText(gui, mc, sellItem, x, y, w, ColorTXT);
                y += h;
                drawText(gui, mc, buyItemText.getString(), x, y, w, ColorTXT & 0x80FFFFFF);
                y += h;
                drawText(gui, mc, buyItem, x, y, w, ColorTXT);
                break;
            case 2:
                drawText(gui, mc, sellItemText.getString(), x, y, w, ColorTXT & 0x80FFFFFF);
                y += h;
                drawText(gui, mc, sellItem, x, y, w, ColorTXT);
                break;
            case 3:
                drawText(gui, mc, buyItemText.getString(), x, y, w, ColorTXT & 0x80FFFFFF);
                y += h;
                drawText(gui, mc, buyItem, x, y, w, ColorTXT);
                break;
        }
        return y;
    }

    private static void drawText(GuiGraphics gui, Minecraft mc, String text, int x, int y, int w, int color) {
        int txtW = mc.font.width(text);
        int txtX = x + (w - txtW) / 2;
        gui.drawString(mc.font, text, txtX, y, color);
    }

    private static void drawText(GuiGraphics gui, Minecraft mc, ItemStack item, int x, int y, int w, int color) {
        String text = formatItemText(item);
        int txtW = mc.font.width(text);
        int totW = 16 + 4 + txtW; 
        int txtX = x + (w - totW) / 2;

        gui.renderItem(item, txtX, y - 5);
        gui.drawString(mc.font, text, txtX + 20, y, color);
    }

    private static class HintDimensions {
        final int width;
        final int height;

        HintDimensions(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    private static class ItemDimensions {
        final int width;

        ItemDimensions(int width) {
            this.width = width;
        }
    }

}
