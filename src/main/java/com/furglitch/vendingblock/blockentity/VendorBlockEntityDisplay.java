package com.furglitch.vendingblock.blockentity;

import com.furglitch.vendingblock.blockentity.renderer.VendorBlockBaseRenderer;
import com.furglitch.vendingblock.blockentity.renderer.VendorBlockItemRenderer;
import com.furglitch.vendingblock.blockentity.renderer.VendorBlockWarningRenderer;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class VendorBlockEntityDisplay implements BlockEntityRenderer<VendorBlockEntity> {

    private final VendorBlockItemRenderer itemRenderer;
    private final VendorBlockBaseRenderer textureRenderer;
    private final VendorBlockWarningRenderer warningRenderer;
    
    public VendorBlockEntityDisplay(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = new VendorBlockItemRenderer();
        this.textureRenderer = new VendorBlockBaseRenderer();
        this.warningRenderer = new VendorBlockWarningRenderer();
    }
    
    public void render(VendorBlockEntity entity, float pTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        
        ItemStack item = entity.inventory.getStackInSlot(0);
        ItemStack textureItem = entity.inventory.getStackInSlot(11);

        if (!textureItem.isEmpty() && textureItem.getItem() instanceof BlockItem blockItem) {
            textureRenderer.renderTextureOverlay(blockItem.getBlock(), poseStack, bufferSource, packedLight, packedOverlay, entity.getLevel(), entity.getBlockPos());
        }

        //if (entity.hasError) warningRenderer.renderErrorCube(entity, poseStack, bufferSource, packedLight, packedOverlay);

        itemRenderer.renderItem(item, poseStack, bufferSource, entity.getLevel(), entity.getBlockPos());
    }

}
