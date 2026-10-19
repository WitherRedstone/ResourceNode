package com.chinaex123.resource_node.init;

import com.chinaex123.resource_node.ResourceNode;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface RNItems {
    DeferredRegister.Items ITEMS = DeferredRegister.createItems(ResourceNode.MODID);

    static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
