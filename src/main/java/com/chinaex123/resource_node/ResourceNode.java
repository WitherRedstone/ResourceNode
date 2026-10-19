package com.chinaex123.resource_node;

import com.chinaex123.resource_node.config.RNConfig;
import com.chinaex123.resource_node.init.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(ResourceNode.MODID)
public class ResourceNode {
    public static final String MODID = "resource_node";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ResourceNode(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, RNConfig.SPEC);
        modEventBus.addListener(this::registerCapabilities);
        RNItems.register(modEventBus);
        RNBlocks.register(modEventBus);
        RNBlockEntities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        RNMenuTypes.register(modEventBus);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                RNBlockEntities.RESOURCE_NODE.get(),
                (be, side) -> be.energyHandler
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                RNBlockEntities.RESOURCE_NODE.get(),
                (be, side) -> be.inventory
        );
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }
}