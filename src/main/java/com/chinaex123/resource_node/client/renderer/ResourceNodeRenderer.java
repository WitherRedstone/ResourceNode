package com.chinaex123.resource_node.client.renderer;

import com.chinaex123.resource_node.blockentity.ResourceNodeBlockEntity;
import com.chinaex123.resource_node.config.RNConfig;
import com.chinaex123.resource_node.util.TimeFormatUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class ResourceNodeRenderer implements BlockEntityRenderer<ResourceNodeBlockEntity> {

    public ResourceNodeRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(ResourceNodeBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int combinedOverlay) {
        if (!RNConfig.RENDER_ENABLED.get()) return;

        ItemStack template = be.inventory.getStackInSlot(0);
        if (template.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        float time = be.getLevel() != null ? be.getLevel().getGameTime() + partialTick : 0;

        float rotationY = (time * 2f) % 360f;
        float bobOffset = (float) Math.sin(time * 0.08) * 0.05f;

        // 获取方块上方的光照
        int light = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().above());

        // 渲染上方旋转的物品
        poseStack.pushPose();
        poseStack.translate(0.5, RNConfig.RENDER_ITEM_Y.get() + bobOffset, 0.5);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        mc.getItemRenderer().renderStatic(
                template, ItemDisplayContext.GUI, light,
                OverlayTexture.NO_OVERLAY, poseStack, bufferSource,
                be.getLevel(), 0
        );
        poseStack.popPose();

        // 渲染倒计时文字
        int remaining = Math.max(0, be.getSpeedInterval() - be.getTickTimer());
        Component text = TimeFormatUtil.formatTicks(remaining);

        float scale = 0.025f;
        float textWidth = mc.font.width(text) * scale;

        // 计算玩家看向方块的 Y 旋转角度
        double dx = mc.gameRenderer.getMainCamera().getPosition().x - (be.getBlockPos().getX() + 0.5);
        double dz = mc.gameRenderer.getMainCamera().getPosition().z - (be.getBlockPos().getZ() + 0.5);
        float cameraYaw = -((float) Math.toDegrees(Math.atan2(dz, dx)) - 90f);

        poseStack.pushPose();
        poseStack.translate(0.5, RNConfig.RENDER_TEXT_Y.get() + bobOffset, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(cameraYaw));
        poseStack.translate(-textWidth / 2.0, 0, 0);
        poseStack.scale(scale, -scale, scale);

        Matrix4f matrix4f = poseStack.last().pose();
        mc.font.drawInBatch(text, 0, 0, 0xFFFFFF, true, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, light);

        poseStack.popPose();
    }
}