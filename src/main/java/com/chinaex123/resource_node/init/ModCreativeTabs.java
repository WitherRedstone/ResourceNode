package com.chinaex123.resource_node.init;

import com.chinaex123.resource_node.ResourceNode;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ResourceNode.MODID);

    public static final Supplier<CreativeModeTab> RESOURCE_NODE_TAB =
            CREATIVE_MODE_TAB.register("resource_node_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(RNBlocks.RESOURCE_NODE.get()))
                    .title(Component.translatable("itemGroup.resource_node_tab"))
                    .displayItems((parameters, output) -> {

                        output.accept(RNBlocks.RESOURCE_NODE.get());

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
