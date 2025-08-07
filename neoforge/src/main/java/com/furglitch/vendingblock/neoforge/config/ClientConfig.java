package com.furglitch.vendingblock.neoforge.config;

import org.jetbrains.annotations.NotNull;

import com.furglitch.vendingblock.VendingBlock;

import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum;
import net.minecraft.resources.ResourceLocation;

@Version(version=1)
public class ClientConfig extends Config {
    public ClientConfig() {
        super(ResourceLocation.fromNamespaceAndPath(VendingBlock.MOD_ID, "config.client"));
    }

    public ValidatedEnum<AnimationMode> animationMode = new ValidatedEnum<>(AnimationMode.SERVER, ValidatedEnum.WidgetType.CYCLING);
    public enum AnimationMode implements EnumTranslatable {
        SERVER,
        STILL,
        ROTATE,
        BOB,
        BOB_ROTATE,
        FACE;

        @NotNull @Override
        public String prefix() {
            return "vendingblock.config.client.animationMode";
        }
    }

    public ValidatedEnum<ScrolltipPosition> scrolltipPosition = new ValidatedEnum<>(ScrolltipPosition.TOP, ValidatedEnum.WidgetType.CYCLING);
    public enum ScrolltipPosition implements EnumTranslatable {
        ITEM,
        TOP;

        @NotNull @Override
        public String prefix() {
            return "vendingblock.config.client.scrolltipPosition";
        }
    }

    public NotificationSection notificationSection = new NotificationSection();
    public static class NotificationSection extends ConfigSection {
        public boolean msgPurchase = true;
        public boolean msgGiveaway = true;
        public boolean msgDonation = true;
        public boolean msgEmpty = true;
        public boolean msgFull = true;
    }
}
