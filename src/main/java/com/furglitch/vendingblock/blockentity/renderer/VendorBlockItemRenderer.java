package com.furglitch.vendingblock.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class VendorBlockItemRenderer {
    
    private float rotation;
    private long lastTime = System.currentTimeMillis();
    
    public void renderItem(ItemStack item, PoseStack poseStack, MultiBufferSource bufferSource, Level level, BlockPos pos) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(getRotation()));
        
        int lightLevel = getLightLevel(level, pos);
        itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, lightLevel, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, level, 1);
        
        poseStack.popPose();
    }
    
    private float getRotation() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastTime;
        lastTime = currentTime;

        rotation += (elapsedTime / 16.0f); // Adjust rotation speed here
        if (rotation >= 360.0f) rotation -= 360.0f;
        return rotation;
    }
    
    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
