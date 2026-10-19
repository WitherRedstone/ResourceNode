package com.chinaex123.resource_node.client;

import com.chinaex123.resource_node.ResourceNode;
import com.chinaex123.resource_node.config.RNConfig;
import com.chinaex123.resource_node.init.RNBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import com.chinaex123.resource_node.util.TimeFormatUtil;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = ResourceNode.MODID, value = Dist.CLIENT)
public class RNTooltip {

    private RNTooltip() {}

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().is(RNBlocks.RESOURCE_NODE.asItem())) {
            var tooltip = event.getToolTip();

            tooltip.add(Component.empty());

            tooltip.add(Component.translatable("tooltip.resource_node.precise_tick")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.translatable(
                            RNConfig.PRECISE_TICK_ADJUST.get()
                                    ? "tooltip.resource_node.enabled"
                                    : "tooltip.resource_node.disabled")
                            .withStyle(ChatFormatting.GOLD)));

            tooltip.add(Component.translatable("tooltip.resource_node.speed_range")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("[").withStyle(ChatFormatting.GOLD))
                    .append(TimeFormatUtil.formatTicks(RNConfig.MIN_SPEED.get()).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal(", ").withStyle(ChatFormatting.GOLD))
                    .append(TimeFormatUtil.formatTicks(RNConfig.MAX_SPEED.get()).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal("]").withStyle(ChatFormatting.GOLD)));

            tooltip.add(Component.translatable("tooltip.resource_node.count_range")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("[" + RNConfig.MIN_COUNT.get() + ", " + RNConfig.MAX_COUNT.get() + "]")
                            .withStyle(ChatFormatting.GOLD)));
        }
    }
}