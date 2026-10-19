package com.chinaex123.resource_node.data;

import com.chinaex123.resource_node.ResourceNode;
import com.chinaex123.resource_node.init.RNBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ResourceNode.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(RNBlocks.RESOURCE_NODE.get(), models()
                .cubeBottomTop("resource_node", modLoc("block/resource_node_side"), modLoc("block/resource_node_soil"), modLoc("block/resource_node_top")
        ));

    }
}
