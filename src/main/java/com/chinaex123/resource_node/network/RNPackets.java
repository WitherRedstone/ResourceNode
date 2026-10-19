package com.chinaex123.resource_node.network;

import com.chinaex123.resource_node.ResourceNode;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = ResourceNode.MODID)
public class RNPackets {

    private RNPackets() {}

    @SubscribeEvent
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(ResourceNode.MODID);

        registrar.playToServer(
                ResourceNodeAdjustPayload.TYPE,
                ResourceNodeAdjustPayload.STREAM_CODEC,
                ResourceNodeAdjustPayload::handle
        );
    }
}