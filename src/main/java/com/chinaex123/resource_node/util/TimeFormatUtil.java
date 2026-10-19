package com.chinaex123.resource_node.util;

import com.chinaex123.resource_node.config.RNConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import java.util.Locale;

public class TimeFormatUtil {

    public static MutableComponent formatTicks(int ticks) {
        if (RNConfig.PRECISE_TICK_ADJUST.get()) {
            return Component.translatable("gui.resource_node.format.ticks", String.valueOf(ticks));
        }

        float seconds = ticks / 20.0f;

        if (seconds < 1) {
            return Component.translatable("gui.resource_node.format.ticks", String.valueOf(ticks));
        }

        if (seconds >= 3600) {
            float hours = seconds / 3600.0f;
            String timeStr = formatNumber(hours);
            return Component.translatable("gui.resource_node.format.hours", timeStr);
        } else if (seconds >= 60) {
            float minutes = seconds / 60.0f;
            String timeStr = formatNumber(minutes);
            return Component.translatable("gui.resource_node.format.minutes", timeStr);
        } else {
            String timeStr = formatNumber(seconds);
            return Component.translatable("gui.resource_node.format.seconds", timeStr);
        }
    }

    private static String formatNumber(float value) {
        if (value == (float) Math.floor(value)) {
            return String.format(Locale.US, "%.0f", value);
        } else {
            return String.format(Locale.US, "%.1f", value);
        }
    }
}