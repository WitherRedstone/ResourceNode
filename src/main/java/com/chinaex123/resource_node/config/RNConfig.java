package com.chinaex123.resource_node.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class RNConfig {

    public enum SpawnMode {
        /** 从上方掉落，物品会弹跳 */
        DROP,
        /** 在顶面随机位置分散生成，不弹跳 */
        SCATTER,
        /** 在顶面正中心生成，不弹跳 */
        CENTER
    }

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue PRECISE_TICK_ADJUST;
    public static final ModConfigSpec.IntValue MIN_SPEED;
    public static final ModConfigSpec.IntValue MAX_SPEED;
    public static final ModConfigSpec.IntValue MIN_COUNT;
    public static final ModConfigSpec.IntValue MAX_COUNT;
    public static final ModConfigSpec.EnumValue<SpawnMode> SPAWN_MODE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("通用配置").push("Common Config");
        PRECISE_TICK_ADJUST = builder
                .comment(
                        "是否开启精确 Tick 调整。",
                        "关闭时：只能按 20t (1秒) 的步长调整，显示为 X秒",
                        "开启时：按 1t 的步长调整，显示为 X tick"
                )
                .define("preciseTickAdjust", false);
        MIN_SPEED = builder
                .comment("每次生成间隔的最小值（tick）")
                .defineInRange("minSpeed", 1, 1, Integer.MAX_VALUE);
        MAX_SPEED = builder
                .comment("每次生成间隔的最大值（tick）")
                .defineInRange("maxSpeed", 72000, 1, Integer.MAX_VALUE);
        MIN_COUNT = builder
                .comment("每次生成物品数量的最小值")
                .defineInRange("minCount", 1, 1, Integer.MAX_VALUE);
        MAX_COUNT = builder
                .comment("每次生成物品数量的最大值")
                .defineInRange("maxCount", 64, 1, Integer.MAX_VALUE);
        SPAWN_MODE = builder
                .comment(
                        "物品生成方式：",
                        "DROP    - 从上方掉落，会弹跳（原版感觉）",
                        "SCATTER - 在顶面随机分散",
                        "CENTER  - 在顶面正中心"
                )
                .defineEnum("spawnMode", SpawnMode.SCATTER);
        builder.pop();

        SPEC = builder.build();
    }
}