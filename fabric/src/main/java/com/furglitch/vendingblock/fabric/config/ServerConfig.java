package com.furglitch.vendingblock.fabric.config;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.furglitch.vendingblock.VendingBlock;

import me.fzzyhmstrs.fzzy_config.annotations.Action;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.annotations.WithPerms;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.util.EnumTranslatable;
import me.fzzyhmstrs.fzzy_config.validation.minecraft.ValidatedIngredient;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

@Version(version=1) @WithPerms(opLevel = 3)
public class ServerConfig extends Config {
    public ServerConfig() {
        super(ResourceLocation.fromNamespaceAndPath(VendingBlock.MOD_ID, "config.server"));
    }

    @RequiresAction(action = Action.RESTART)
    public boolean creativeKey = true;

    public ValidatedEnum<BreakLevel> breakLevel = new ValidatedEnum<>(BreakLevel.GAMEMASTER, ValidatedEnum.WidgetType.CYCLING);
    public enum BreakLevel implements EnumTranslatable {
        SERVER_OWNER,
        ADMIN,
        GAMEMASTER,
        MODERATOR,
        BLOCK_OWNER_ONLY;

        @NotNull @Override
        public String prefix() {
            return "vendingblock.config.server.breakLevel";
        }
    }

    public ValidatedEnum<AnimationMode> animationMode = new ValidatedEnum<>(AnimationMode.ROTATE, ValidatedEnum.WidgetType.CYCLING);
    public enum AnimationMode implements EnumTranslatable {
        STILL,
        ROTATE,
        BOB,
        BOB_ROTATE,
        FACE;

        @NotNull @Override
        public String prefix() {
            return "vendingblock.config.server.animationMode";
        }
    }

    @RequiresAction(action = Action.RESTART)
    public ValidatedIngredient productBlacklist = new ValidatedIngredient(Set.of(
        ResourceLocation.fromNamespaceAndPath("vendingblock", "vendor_key")
    ));

    @RequiresAction(action = Action.RESTART)
    public ValidatedIngredient facadeBlacklist = new ValidatedIngredient(Set.of(
        TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "bookshelves")),
        TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "glass_blocks")),
        TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "leaves")),
        ResourceLocation.withDefaultNamespace("beacon"),
        ResourceLocation.withDefaultNamespace("cobweb"),
        ResourceLocation.withDefaultNamespace("copper_grate"),
        ResourceLocation.withDefaultNamespace("exposed_copper_grate"),
        ResourceLocation.withDefaultNamespace("glow_lichen"),
        ResourceLocation.withDefaultNamespace("mangrove_roots"),
        ResourceLocation.withDefaultNamespace("oxidized_copper_grate"),
        ResourceLocation.withDefaultNamespace("piston"),
        ResourceLocation.withDefaultNamespace("sculk_vein"),
        ResourceLocation.withDefaultNamespace("sticky_piston"),
        ResourceLocation.withDefaultNamespace("vine"),
        ResourceLocation.withDefaultNamespace("waxed_copper_grate"),
        ResourceLocation.withDefaultNamespace("waxed_exposed_copper_grate"),
        ResourceLocation.withDefaultNamespace("waxed_oxidized_copper_grate"),
        ResourceLocation.withDefaultNamespace("waxed_weathered_copper_grate"),
        ResourceLocation.withDefaultNamespace("weathered_copper_grate")
    ));

}
