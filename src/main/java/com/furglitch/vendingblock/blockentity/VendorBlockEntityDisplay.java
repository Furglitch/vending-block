package com.furglitch.vendingblock.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;

public class VendorBlockEntityDisplay implements BlockEntityRenderer<VendorBlockEntity> {

    private float rotation;
    public VendorBlockEntityDisplay(BlockEntityRendererProvider.Context context) {}
    
    public void render(VendorBlockEntity entity, float pTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack item = entity.inventory.getStackInSlot(0);
        ItemStack textureItem = entity.inventory.getStackInSlot(11);

        if (!textureItem.isEmpty() && textureItem.getItem() instanceof BlockItem blockItem) {
            renderTextureOverlay(entity, poseStack, bufferSource, packedLight, packedOverlay, blockItem.getBlock());
        }

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(getRotation()));
        itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, getLightLevel(entity.getLevel(), entity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack, bufferSource, entity.getLevel(), 1);
        poseStack.popPose();
    }

    private void renderTextureOverlay(VendorBlockEntity entity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Block block) {

        ResourceLocation blockID = BuiltInRegistries.BLOCK.getKey(block);
        ResourceLocation atlasLoc = ResourceLocation.withDefaultNamespace("textures/atlas/blocks.png");
        
        TextureAtlasSprite sprite = getBlockTexture(blockID, Minecraft.getInstance().getModelManager().getAtlas(atlasLoc));
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.cutout());
        
        poseStack.pushPose();
        
        int tintColor = getBiomeColor(block, entity.getLevel(), entity.getBlockPos());
        
        renderCube(poseStack, vertexConsumer, -0.00001f, -0.00001f, -0.00001f, 1.00001f, 0.12501f, 1.00001f, sprite, packedLight, packedOverlay, tintColor);
        
        poseStack.popPose();

    }

    private void renderCube(PoseStack poseStack, VertexConsumer vertexConsumer, float x1, float y1, float z1, float x2, float y2, float z2, TextureAtlasSprite sprite, int packedLight, int packedOverlay, int tintColor) {
        
        var matrix = poseStack.last().pose();
        
        float heightRatio = (y2 - y1) / 1.0f;
        float vMin = sprite.getV1() - (sprite.getV1() - sprite.getV0()) * heightRatio;
        float vMax = sprite.getV1();
        
        float textureStretch = -0.00001f;
        float uMin = sprite.getU0() - textureStretch;
        float uMax = sprite.getU1() + textureStretch;
        float vMinStretched = vMin - textureStretch;
        float vMaxStretched = vMax + textureStretch;
        float vTopMin = sprite.getV0() - textureStretch;
        float vTopMax = sprite.getV1() + textureStretch;
        
        // Extract RGB components from tint color
        int red = (tintColor >> 16) & 0xFF;
        int green = (tintColor >> 8) & 0xFF;
        int blue = tintColor & 0xFF;
        
        // Bottom
        addVertex(matrix, vertexConsumer, x1, y1, z1, uMin, vTopMin, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x2, y1, z1, uMax, vTopMin, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x2, y1, z2, uMax, vTopMax, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x1, y1, z2, uMin, vTopMax, packedLight, packedOverlay, red, green, blue);
        
        // Top
        addVertex(matrix, vertexConsumer, x1, y2, z2, uMin, vTopMax, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x2, y2, z2, uMax, vTopMax, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x2, y2, z1, uMax, vTopMin, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x1, y2, z1, uMin, vTopMin, packedLight, packedOverlay, red, green, blue);
        
        // North
        addVertex(matrix, vertexConsumer, x1, y1, z1, uMax, vMaxStretched, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x1, y2, z1, uMax, vMinStretched, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x2, y2, z1, uMin, vMinStretched, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x2, y1, z1, uMin, vMaxStretched, packedLight, packedOverlay, red, green, blue);
        
        // South
        addVertex(matrix, vertexConsumer, x2, y1, z2, uMax, vMaxStretched, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x2, y2, z2, uMax, vMinStretched, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x1, y2, z2, uMin, vMinStretched, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x1, y1, z2, uMin, vMaxStretched, packedLight, packedOverlay, red, green, blue);
        
        // West
        addVertex(matrix, vertexConsumer, x1, y1, z2, uMax, vMaxStretched, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x1, y2, z2, uMax, vMinStretched, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x1, y2, z1, uMin, vMinStretched, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x1, y1, z1, uMin, vMaxStretched, packedLight, packedOverlay, red, green, blue);
        
        // East
        addVertex(matrix, vertexConsumer, x2, y1, z1, uMax, vMaxStretched, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x2, y2, z1, uMax, vMinStretched, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x2, y2, z2, uMin, vMinStretched, packedLight, packedOverlay, red, green, blue);
        addVertex(matrix, vertexConsumer, x2, y1, z2, uMin, vMaxStretched, packedLight, packedOverlay, red, green, blue);
    }

    private void addVertex(org.joml.Matrix4f matrix, VertexConsumer vertexConsumer, float x, float y, float z, float u, float v, int packedLight, int packedOverlay, int red, int green, int blue) {
        vertexConsumer.addVertex(matrix, x, y, z)
            .setColor(red, green, blue, 255)
            .setUv(u, v)
            .setOverlay(packedOverlay)
            .setLight(packedLight)
            .setNormal(0.0f, 1.0f, 0.0f);
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

    private TextureAtlasSprite getBlockTexture(ResourceLocation blockID, net.minecraft.client.renderer.texture.TextureAtlas atlas) {
        String[] texturePatterns = {
            "block/" + blockID.getPath(),
            "block/" + blockID.getPath() + "_top",
            "block/" + blockID.getPath() + "_side",
            "block/" + blockID.getPath() + "_back",
            "block/" + blockID.getPath() + "_front",
            "block/" + blockID.getPath() + "_0",
            "block/" + blockID.getPath() + "_still"
        };
        
        for (String pattern : texturePatterns) {
            ResourceLocation textureLoc = ResourceLocation.fromNamespaceAndPath(blockID.getNamespace(), pattern);
            TextureAtlasSprite sprite = atlas.getSprite(textureLoc);
            if (!sprite.contents().name().getPath().contains("missingno")) {
                return sprite;
            }
        }
        
        ResourceLocation fallbackLoc = ResourceLocation.fromNamespaceAndPath(blockID.getNamespace(), "block/" + blockID.getPath());
        return atlas.getSprite(fallbackLoc);
    }

    private int getBiomeColor(Block block, Level level, BlockPos pos) {
        
        if (level == null) return 0xFFFFFF;
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        String blockPath = blockId.getPath();
        
        if (blockPath.contains("grass_block") || blockPath.contains("grass")) {
            return Minecraft.getInstance().getBlockColors().getColor(block.defaultBlockState(), level, pos, 0);
        } else if (blockPath.contains("leaves")) {
            return Minecraft.getInstance().getBlockColors().getColor(block.defaultBlockState(), level, pos, 0);
        }
        
        return 0xFFFFFF;

    }

}
