package com.chinaex123.resource_node.network;

import com.chinaex123.resource_node.ResourceNode;
import com.chinaex123.resource_node.client.menu.ResourceNodeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ResourceNodeAdjustPayload(
        BlockPos blockPos,
        int modeOrdinal,
        int delta
) implements CustomPacketPayload {

    public enum Mode {SPEED, COUNT}

    public static final Type<ResourceNodeAdjustPayload> TYPE =
            new Type<>(ResourceNode.id("adjust"));

    public static final StreamCodec<FriendlyByteBuf, ResourceNodeAdjustPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, ResourceNodeAdjustPayload::blockPos,
                    ByteBufCodecs.INT, ResourceNodeAdjustPayload::modeOrdinal,
                    ByteBufCodecs.INT, ResourceNodeAdjustPayload::delta,
                    ResourceNodeAdjustPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final ResourceNodeAdjustPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player rawPlayer = context.player();
            if (!(rawPlayer instanceof ServerPlayer player)) return;
            if (!(player.containerMenu instanceof ResourceNodeMenu menu)) return;

            if (!menu.getBlockPos().equals(payload.blockPos())) return;

            Mode mode = Mode.values()[payload.modeOrdinal()];
            switch (mode) {
                case SPEED -> menu.adjustSpeed(payload.delta());
                case COUNT -> menu.adjustCount(payload.delta());
            }
        });
    }
}