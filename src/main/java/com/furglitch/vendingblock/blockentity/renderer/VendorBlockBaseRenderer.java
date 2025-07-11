package com.furglitch.vendingblock.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class VendorBlockBaseRenderer {

    private static final float TEXTURE_STRETCH = -0.0005f;
    
    public void renderTextureOverlay(Block block, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Level level, BlockPos pos) {
        
        ResourceLocation blockID = BuiltInRegistries.BLOCK.getKey(block);
        ResourceLocation atlasLoc = ResourceLocation.withDefaultNamespace("textures/atlas/blocks.png");
        
        TextureAtlasSprite sprite = getBlockTexture(blockID, Minecraft.getInstance().getModelManager().getAtlas(atlasLoc));
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.cutout());
        
        poseStack.pushPose();
        
        int tintColor = getBiomeColor(block, level, pos);
        renderCube(poseStack, vertexConsumer, TEXTURE_STRETCH, TEXTURE_STRETCH, TEXTURE_STRETCH, 1f - TEXTURE_STRETCH, 0.125f - TEXTURE_STRETCH, 1f - TEXTURE_STRETCH, sprite, packedLight, packedOverlay, tintColor);
        
        poseStack.popPose();
    }
    
    private void renderCube(PoseStack poseStack, VertexConsumer vertexConsumer, float x1, float y1, float z1, float x2, float y2, float z2, TextureAtlasSprite sprite, int packedLight, int packedOverlay, int tintColor) {
        
        var matrix = poseStack.last().pose();
        float heightRatio = (y2 - y1) / 1.0f;
        float uMin = sprite.getU0() - TEXTURE_STRETCH;
        float uMax = sprite.getU1() + TEXTURE_STRETCH;
        float vMinStretched = (sprite.getV1() - (sprite.getV1() - sprite.getV0()) * heightRatio) - TEXTURE_STRETCH;
        float vMaxStretched = sprite.getV1() + TEXTURE_STRETCH;
        float vTopMin = sprite.getV0() - TEXTURE_STRETCH;
        float vTopMax = sprite.getV1() + TEXTURE_STRETCH;
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
    
    private TextureAtlasSprite getBlockTexture(ResourceLocation blockID, net.minecraft.client.renderer.texture.TextureAtlas atlas) {
        String[] texturePatterns = {
            "block/" + blockID.getPath(),
            "block/" + blockID.getPath() + "_top",
            "block/" + blockID.getPath() + "_side",
            "block/" + blockID.getPath() + "_back",
            "block/" + blockID.getPath() + "_front",
            "block/" + blockID.getPath() + "_0",
            "block/" + blockID.getPath() + "_still",
            "block/" + blockID.getPath() + "_on",
            "block/" + blockID.getPath() + "_active",
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
