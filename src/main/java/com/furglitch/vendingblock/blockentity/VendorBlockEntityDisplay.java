package com.furglitch.vendingblock.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class VendorBlockEntityDisplay implements BlockEntityRenderer<VendorBlockEntity> {

    private float rotation;
    public VendorBlockEntityDisplay(BlockEntityRendererProvider.Context context) {}
    
    public void render(VendorBlockEntity entity, float pTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack item = entity.inventory.getStackInSlot(0);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(getRotation()));
        itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, getLightLevel(entity.getLevel(), entity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.getLevel(), 1);
        poseStack.popPose();

    }
    private float getRotation() {
        rotation += 0.5f;
        if (rotation >= 360.0f) rotation = 0.0f;
        return rotation;
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
