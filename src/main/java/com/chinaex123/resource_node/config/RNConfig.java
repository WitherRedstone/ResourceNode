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
    public static final ModConfigSpec.BooleanValue RENDER_ENABLED;
    public static final ModConfigSpec.DoubleValue RENDER_ITEM_Y;
    public static final ModConfigSpec.DoubleValue RENDER_TEXT_Y;
    public static final ModConfigSpec.BooleanValue ENERGY_ENABLED;
    public static final ModConfigSpec.IntValue ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue ENERGY_MIN_COST;
    public static final ModConfigSpec.IntValue ENERGY_MAX_COST;

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

        ENERGY_ENABLED = builder
                .comment("是否启用 FE 能量消耗。")
                .define("energyEnabled", true);
        ENERGY_CAPACITY = builder
                .comment("FE 能量容量")
                .defineInRange("energyCapacity", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
        ENERGY_MIN_COST = builder
                .comment("最低 FE 消耗（最低速+最少数量时）；消耗按指数增长，最高消耗为 maxCost")
                .defineInRange("energyMinCost", 50000, 1, Integer.MAX_VALUE);
        ENERGY_MAX_COST = builder
                .comment("最高 FE 消耗（最高速+最多数量时）；消耗按指数增长，最低消耗为 minCost")
                .defineInRange("energyMaxCost", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);

        builder.pop();

        builder.comment("渲染配置").push("Render Config");
        RENDER_ENABLED = builder
                .comment("是否开启方块上方物品和倒计时的渲染")
                .define("renderEnabled", true);
        RENDER_ITEM_Y = builder
                .comment("物品渲染的 Y 偏移")
                .defineInRange("renderItemY", 2.0, 1.0, 10.0);
        RENDER_TEXT_Y = builder
                .comment("倒计时文字的 Y 偏移")
                .defineInRange("renderTextY", 1.70, 1.0, 10.0);
        builder.pop();

        SPEC = builder.build();
    }
}