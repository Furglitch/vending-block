package com.furglitch.vendingblock.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
    
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(OwnerChangePacket.TYPE, OwnerChangePacket.STREAM_CODEC, OwnerChangePacket::handle);
    }
}
