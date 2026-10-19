package com.chinaex123.resource_node.init;

import com.chinaex123.resource_node.ResourceNode;
import com.chinaex123.resource_node.blockentity.ResourceNodeBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface RNBlockEntities {
    DeferredRegister<BlockEntityType<?>> BLOCKS = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ResourceNode.MODID);

    /** 资源节点块实体类型 */
    Supplier<BlockEntityType<ResourceNodeBlockEntity>> RESOURCE_NODE =
            BLOCKS.register("resource_node", () -> BlockEntityType.Builder.of(
                    ResourceNodeBlockEntity::new,
                    RNBlocks.RESOURCE_NODE.get()
            ).build(null));

    static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}