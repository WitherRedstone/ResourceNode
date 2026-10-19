package com.chinaex123.resource_node.client;

import com.chinaex123.resource_node.ResourceNode;
import com.chinaex123.resource_node.client.gui.ResourceNodeScreen;
import com.chinaex123.resource_node.client.renderer.ResourceNodeRenderer;
import com.chinaex123.resource_node.init.RNBlockEntities;
import com.chinaex123.resource_node.init.RNMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ResourceNode.MODID, value = Dist.CLIENT)
public class RNScreens {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(RNMenuTypes.RESOURCE_NODE_MENU.get(), ResourceNodeScreen::new);
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                RNBlockEntities.RESOURCE_NODE.get(),
                ResourceNodeRenderer::new
        );
    }
}