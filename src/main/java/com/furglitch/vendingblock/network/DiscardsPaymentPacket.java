package com.furglitch.vendingblock.network;

import com.furglitch.vendingblock.blockentity.VendorBlockEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DiscardsPaymentPacket(BlockPos pos, boolean discardsPayment) implements CustomPacketPayload {
    
    public static final Type<DiscardsPaymentPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("vendingblock", "discards_payment"));
    
    public static final StreamCodec<ByteBuf, DiscardsPaymentPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, DiscardsPaymentPacket::pos,
        ByteBufCodecs.BOOL, DiscardsPaymentPacket::discardsPayment,
        DiscardsPaymentPacket::new
    );
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(DiscardsPaymentPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Level level = player.level();
            
            if (level.getBlockEntity(packet.pos()) instanceof VendorBlockEntity vendorBlockEntity) {
                vendorBlockEntity.setDiscarding(packet.discardsPayment());
            }
        });
    }
}
