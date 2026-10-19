package com.chinaex123.resource_node.client.gui;

import com.chinaex123.resource_node.ResourceNode;
import com.chinaex123.resource_node.client.menu.ResourceNodeMenu;
import com.chinaex123.resource_node.config.RNConfig;
import com.chinaex123.resource_node.network.ResourceNodeAdjustPayload;
import com.chinaex123.resource_node.util.TimeFormatUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ResourceNodeScreen extends AbstractContainerScreen<ResourceNodeMenu> {

    private static final ResourceLocation TEXTURE = ResourceNode.id("textures/gui/resource_node.png");
    private static final ResourceLocation ENERGY_EMPTY = ResourceNode.id("textures/gui/energy_empty.png");
    private static final ResourceLocation ENERGY_FILLED = ResourceNode.id("textures/gui/energy_filled.png");

    private static final int IMAGE_WIDTH = 175;
    private static final int IMAGE_HEIGHT = 165;

    private static final int SPEED_MINUS_X = 80;
    private static final int SPEED_PLUS_X = 134;
    private static final int SPEED_Y = 32;

    private static final int COUNT_MINUS_X = 80;
    private static final int COUNT_PLUS_X = 134;
    private static final int COUNT_Y = 54;

    private static final int BUTTON_W = 16;
    private static final int BUTTON_H = 16;

    private static final int ENERGY_BAR_X = 7;
    private static final int ENERGY_BAR_Y = 11;
    private static final int ENERGY_BAR_WIDTH = 10;
    private static final int ENERGY_BAR_HEIGHT = 58;

    public ResourceNodeScreen(ResourceNodeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = IMAGE_WIDTH;
        this.imageHeight = IMAGE_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        // 速度行
        this.addRenderableWidget(makeAdjustButton(
                this.leftPos + SPEED_MINUS_X,
                this.topPos + SPEED_Y,
                Component.literal("-"),
                () -> sendAdjust(ResourceNodeAdjustPayload.Mode.SPEED, -1)
        ));
        this.addRenderableWidget(makeAdjustButton(
                this.leftPos + SPEED_PLUS_X,
                this.topPos + SPEED_Y,
                Component.literal("+"),
                () -> sendAdjust(ResourceNodeAdjustPayload.Mode.SPEED, +1)
        ));

        // 数量行
        this.addRenderableWidget(makeAdjustButton(
                this.leftPos + COUNT_MINUS_X,
                this.topPos + COUNT_Y,
                Component.literal("-"),
                () -> sendAdjust(ResourceNodeAdjustPayload.Mode.COUNT, -1)
        ));
        this.addRenderableWidget(makeAdjustButton(
                this.leftPos + COUNT_PLUS_X,
                this.topPos + COUNT_Y,
                Component.literal("+"),
                () -> sendAdjust(ResourceNodeAdjustPayload.Mode.COUNT, +1)
        ));
    }

    private Button makeAdjustButton(int x, int y, Component msg, Runnable onPress) {
        return new CustomButton(x, y, BUTTON_W, BUTTON_H, msg, b -> onPress.run());
    }

    private void sendAdjust(ResourceNodeAdjustPayload.Mode mode, int delta) {
        if (Screen.hasShiftDown()) {
            delta *= 10;
        } else if (Screen.hasControlDown()) {
            delta *= 100;
        }

        PacketDistributor.sendToServer(new ResourceNodeAdjustPayload(
                this.menu.getBlockPos(), mode.ordinal(), delta));
    }

    private class CustomButton extends Button {
        private static final int NORMAL_COLOR = new Color(90, 94, 120).getRGB();
        private static final int HOVER_COLOR = new Color(122, 126, 152).getRGB();
        private static final int TEXT_COLOR = new Color(224, 228, 248).getRGB();
        private static final int BORDER_COLOR = new Color(58, 62, 88).getRGB();

        public CustomButton(int x, int y, int width, int height, Component message, OnPress onPress) {
            super(x, y, width, height, message, onPress, Button.DEFAULT_NARRATION);
        }

        @Override
        public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int bg = isHovered ? HOVER_COLOR : NORMAL_COLOR;
            guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, bg);
            guiGraphics.fill(getX(), getY(), getX() + 1, getY() + height, BORDER_COLOR);
            guiGraphics.fill(getX() + width - 1, getY(), getX() + width, getY() + height, BORDER_COLOR);
            guiGraphics.fill(getX(), getY(), getX() + width, getY() + 1, BORDER_COLOR);
            guiGraphics.fill(getX(), getY() + height - 1, getX() + width, getY() + height, BORDER_COLOR);
            int tc = isHovered ? 0xFFFFFF : TEXT_COLOR;
            guiGraphics.drawCenteredString(font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, tc);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        int energyX = x + ENERGY_BAR_X;
        int energyY = y + ENERGY_BAR_Y;

        RenderSystem.setShaderTexture(0, ENERGY_EMPTY);
        guiGraphics.blit(ENERGY_EMPTY, energyX, energyY, 0, 0, ENERGY_BAR_WIDTH, ENERGY_BAR_HEIGHT, ENERGY_BAR_WIDTH, ENERGY_BAR_HEIGHT);

        int energyStored = this.menu.getClientEnergyStored();
        int energyCapacity = this.menu.getClientEnergyCapacity();
        if (energyCapacity > 0) {
            int filledHeight = Math.round((float) energyStored / energyCapacity * ENERGY_BAR_HEIGHT);
            if (filledHeight > 0) {
                int filledY = energyY + ENERGY_BAR_HEIGHT - filledHeight;
                int textureV = ENERGY_BAR_HEIGHT - filledHeight;
                RenderSystem.setShaderTexture(0, ENERGY_FILLED);
                guiGraphics.blit(ENERGY_FILLED, energyX, filledY, 0, textureV, ENERGY_BAR_WIDTH, filledHeight, ENERGY_BAR_WIDTH, ENERGY_BAR_HEIGHT);
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.renderEnergyTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderEnergyTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int energyX = x + ENERGY_BAR_X;
        int energyY = y + ENERGY_BAR_Y;

        if (mouseX >= energyX && mouseX < energyX + ENERGY_BAR_WIDTH
                && mouseY >= energyY && mouseY < energyY + ENERGY_BAR_HEIGHT) {
            int stored = this.menu.getClientEnergyStored();
            int capacity = this.menu.getClientEnergyCapacity();
            guiGraphics.renderTooltip(this.font, Component.literal(formatEnergy(stored) + " / " + formatEnergy(capacity)), mouseX, mouseY);
        }
    }

    private String formatEnergy(int energy) {
        if (energy >= 1_000_000_000) {
            return String.format("%.1f GFE", energy / 1_000_000_000.0);
        } else if (energy >= 1_000_000) {
            return String.format("%.1f MFE", energy / 1_000_000.0);
        } else if (energy >= 1_000) {
            return String.format("%.1f kFE", energy / 1_000.0);
        } else {
            return energy + " FE";
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 顶部标题
        guiGraphics.drawString(this.font, this.title, (imageWidth - this.font.width(this.title)) / 2, 6, 4210752, false);
        // 玩家物品栏标题
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, 72, 4210752, false);

        Component speedText = Component.translatable("gui.resource_node.speed_interval");
        Component countText = Component.translatable("gui.resource_node.output_count");

        // 文字右侧距离 "-" 按钮左边缘的像素距离
        int textGap = 4;

        // 绘制“刷新间隔”
        int speedTextX = SPEED_MINUS_X - this.font.width(speedText) - textGap;
        guiGraphics.drawString(this.font, speedText, speedTextX, SPEED_Y + 4, 4210752, false);

        // 绘制“每次数量”
        int countTextX = COUNT_MINUS_X - this.font.width(countText) - textGap;
        guiGraphics.drawString(this.font, countText, countTextX, COUNT_Y + 4, 4210752, false);

        // 速度值显示
        int speedValue = this.menu.getClientSpeed();
        Component speedComponent = TimeFormatUtil.formatTicks(speedValue);
        int speedCenterX = (SPEED_MINUS_X + BUTTON_W + SPEED_PLUS_X) / 2;
        guiGraphics.drawCenteredString(this.font, speedComponent, speedCenterX, SPEED_Y + 4, 0xFFFFFF);

        // 数量值显示
        int countValue = this.menu.getClientCount();
        int countCenterX = (COUNT_MINUS_X + BUTTON_W + COUNT_PLUS_X) / 2;
        guiGraphics.drawCenteredString(this.font, String.valueOf(countValue), countCenterX, COUNT_Y + 4, 0xFFFFFF);

        // 动态显示当前是按秒还是按 tick
        String modeHint = RNConfig.PRECISE_TICK_ADJUST.get() ? "(tick)" : "(s)";
        int hintX = SPEED_PLUS_X + BUTTON_W + 4;
        int hintY = SPEED_Y + 4;
        guiGraphics.drawString(this.font, modeHint, hintX, hintY, 0x808080, false);
    }
}