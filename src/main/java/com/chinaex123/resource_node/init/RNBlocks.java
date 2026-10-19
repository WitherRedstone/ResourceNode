package com.chinaex123.resource_node.init;

import com.chinaex123.resource_node.ResourceNode;
import com.chinaex123.resource_node.block.ResourceNodeBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface RNBlocks {
    DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ResourceNode.MODID);

    /** 资源节点 */
    DeferredBlock<Block> RESOURCE_NODE = registerBlocks("resource_node",
            () -> new ResourceNodeBlock(BlockBehaviour.Properties.of()
                    .strength(1.5F, 6.0F)
                    .mapColor(MapColor.STONE)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    static <T extends Block> void registerBlockItems(String name, DeferredBlock<T> block) {
        RNItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    static <T extends Block> DeferredBlock<T> registerBlocks(String name, Supplier<T> block) {
        DeferredBlock<T> blocks = BLOCKS.register(name, block);
        registerBlockItems(name, blocks);
        return blocks;
    }

    static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
