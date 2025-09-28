package com.furglitch.vendingblock.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class CustomText {

    private CustomText() {}

    public static void drawLeft(GuiGraphics graphics, Font font, Component text, int x, int y, float scale, int color) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(scale, scale, 1.0f);

        String str = text.getString();

        graphics.drawString(font, str, 0, 0, color, false);

        pose.popPose();
    }

    public static void drawCenter(GuiGraphics graphics, Font font, Component text, int x, int y, float scale, int color) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(scale, scale, 1.0f);

        String str = text.getString();
        int textWidth = font.width(str);
        int drawX = - (textWidth / 2);

        graphics.drawString(font, str, drawX, 0, color, false);

        pose.popPose();
    }

    public static void drawScroll(GuiGraphics graphics, Font font, Component text, int x, int y, float scale, int color, int boxWidth, double duration) {
        if (boxWidth <= 0) {
            drawLeft(graphics, font, text, x, y, scale, color);
            return;
        }

        String str = text.getString();
        int textWidth = font.width(str);
        float textWidthScaled = textWidth * scale;

        int scissorLeft = x;
        int scissorTop = y;
        int scissorRight = x + boxWidth;
        int scissorBottom = y + (int) (font.lineHeight * scale);

        if (textWidthScaled <= boxWidth) {
            try {
                graphics.enableScissor(scissorLeft, scissorTop, scissorRight, scissorBottom);
                PoseStack pose = graphics.pose();
                pose.pushPose();
                pose.translate(x, y, 0);
                pose.scale(scale, scale, 1.0f);

                graphics.drawString(font, str, 0, 0, color, false);

                pose.popPose();
            } finally {
                graphics.disableScissor();
            }
            return;
        }

    float gap = 12.0f;
    float totalLength = textWidthScaled + gap;
    if (totalLength <= 0) totalLength = 1.0f;
    double now = System.currentTimeMillis() / 1000.0;
    double speed = totalLength / duration;
    double offsetD = (now * speed) % totalLength;
    float offset = (float) offsetD;

        try {
            graphics.enableScissor(scissorLeft, scissorTop, scissorRight, scissorBottom);

            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate(x, y, 0);
            pose.scale(scale, scale, 1.0f);

            int drawX1 = -Math.round(offset / scale);
            graphics.drawString(font, str, drawX1, 0, color, false);
            int drawX2 = drawX1 + Math.round(totalLength / scale);
            graphics.drawString(font, str, drawX2, 0, color, false);

            pose.popPose();
        } finally {
            graphics.disableScissor();
        }
    }
}
