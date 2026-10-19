package com.chinaex123.resource_node.util;

import com.chinaex123.resource_node.config.RNConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ResourceNodeFilter {

    private static final String[] ADMIN_ITEMS = {
            "minecraft:command_block",
            "minecraft:chain_command_block",
            "minecraft:repeating_command_block",
            "minecraft:command_block_minecart",
            "minecraft:jigsaw",
            "minecraft:structure_block",
            "minecraft:structure_void",
            "minecraft:barrier",
            "minecraft:debug_stick",
            "minecraft:light",
            "minecraft:bedrock",
            "resource_node:*"
    };

    public static boolean canDuplicate(ItemStack stack) {
        if (stack.isEmpty()) return false;

        String itemId = getItemId(stack);
        String modId = itemId.split(":")[0];

        for (String adminItem : ADMIN_ITEMS) {
            if (adminItem.endsWith(":*")) {
                String targetModId = adminItem.substring(0, adminItem.length() - 2);
                if (modId.equals(targetModId)) return false;
            } else if (itemId.equals(adminItem)) {
                return false;
            }
        }

        List<? extends String> filterList = RNConfig.FILTER_LIST.get();
        boolean isBlacklistMode = RNConfig.BLACKLIST_MODE.get();

        if (isBlacklistMode) {
            return !isInList(itemId, modId, stack, filterList);
        } else {
            return isInList(itemId, modId, stack, filterList);
        }
    }

    private static boolean isInList(String itemId, String modId, ItemStack stack, List<? extends String> list) {
        for (String entry : list) {
            if (entry.equals(itemId)) return true;

            if (entry.startsWith("@") && modId.equals(entry.substring(1))) return true;

            if (entry.startsWith("#")) {
                try {
                    ResourceLocation tagLocation = ResourceLocation.parse(entry.substring(1));
                    TagKey<Item> tag = TagKey.create(net.minecraft.core.registries.Registries.ITEM, tagLocation);
                    if (stack.is(tag)) return true;
                } catch (Exception ignored) {}
            }
        }
        return false;
    }

    private static String getItemId(ItemStack stack) {
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return location.toString();
    }
}