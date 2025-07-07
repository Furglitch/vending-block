package com.furglitch.vendingblock.integration.jade;

import com.furglitch.vendingblock.blockentity.VendorBlockEntity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum VendorBlockComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof VendorBlockEntity) {
            CompoundTag serverData = accessor.getServerData();

            boolean hasProduct = serverData.contains("productName");
            boolean hasPrice = serverData.contains("priceName");

            if (hasProduct) {
                String product = serverData.getString("productName");
                int count = serverData.getInt("productCount");
                String text = count > 1 ? count + "x " + product : product;

                if (hasPrice) {
                    tooltip.add(Component.translatable("ui.vendingblock.jade.sell", text));
                } else {
                    tooltip.add(Component.translatable("ui.vendingblock.jade.giveaway", text));
                }
            }

            if (hasPrice) {
                String product = serverData.getString("priceName");
                int count = serverData.getInt("priceCount");
                String text = count > 1 ? count + "x " + product : product;

                if (hasProduct) {
                    tooltip.add(Component.translatable("ui.vendingblock.jade.buy", text));
                } else {
                    tooltip.add(Component.translatable("ui.vendingblock.jade.request", text));
                }
            }

            if (serverData.contains("owner")) tooltip.add(Component.translatable("ui.vendingblock.jade.owner", serverData.getString("owner")));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("vendingblock", "vendor_component");
    }

}