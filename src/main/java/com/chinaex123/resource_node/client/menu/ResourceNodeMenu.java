package com.chinaex123.resource_node.client.menu;

import com.chinaex123.resource_node.blockentity.ResourceNodeBlockEntity;
import com.chinaex123.resource_node.config.RNConfig;
import com.chinaex123.resource_node.init.RNMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ResourceNodeMenu extends AbstractContainerMenu {

    public static final int SLOT_COUNT = 1;

    /** 模板物品槽位置 */
    private static final int INPUT_SLOT_X = 26;
    private static final int INPUT_SLOT_Y = 12;

    /** 玩家物品栏起始位置 */
    private static final int PLAYER_INV_X = 8;
    private static final int PLAYER_INV_Y = 84;

    private final IItemHandler inventory;
    private final BlockPos blockPos;
    private final ResourceNodeBlockEntity blockEntity;

    private final DataSlot speedSlot;
    private final DataSlot countSlot;
    private final DataSlot energyStoredSlot;
    private final DataSlot energyCapacitySlot;

    // 用于接收服务端同步的数据
    private int clientSpeed = 20;
    private int clientCount = 1;
    private int clientEnergyStored = 0;
    private int clientEnergyCapacity = 1;

    public ResourceNodeMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory,
                new ItemStackHandler(1),
                buf.readBlockPos(),
                null);
    }

    public ResourceNodeMenu(int containerId, Inventory playerInventory, IItemHandler blockInventory,
                            BlockPos blockPos, ResourceNodeBlockEntity blockEntity) {
        super(RNMenuTypes.RESOURCE_NODE_MENU.get(), containerId);
        this.inventory = blockInventory;
        this.blockPos = blockPos;
        this.blockEntity = blockEntity;

        // 模板物品输入槽
        this.addSlot(new SlotItemHandler(blockInventory, 0, INPUT_SLOT_X, INPUT_SLOT_Y) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        // 玩家物品栏
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(
                        playerInventory,
                        col + row * 9 + 9,
                        PLAYER_INV_X + col * 18,
                        PLAYER_INV_Y + row * 18
                ));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(
                    playerInventory,
                    col,
                    PLAYER_INV_X + col * 18,
                    PLAYER_INV_Y + 3 * 18 + 4
            ));
        }

        // 服务端把数据同步到客户端
        this.speedSlot = this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity != null ? blockEntity.getSpeedInterval() : clientSpeed;
            }

            @Override
            public void set(int pValue) {
                // 客户端接收到服务端的同步包时，更新缓存
                clientSpeed = pValue;
            }
        });

        this.countSlot = this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity != null ? blockEntity.getOutputCount() : clientCount;
            }

            @Override
            public void set(int pValue) {
                clientCount = pValue;
            }
        });

        this.energyStoredSlot = this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity != null ? blockEntity.getEnergyStored() : clientEnergyStored;
            }

            @Override
            public void set(int pValue) {
                clientEnergyStored = pValue;
            }
        });

        this.energyCapacitySlot = this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity != null ? RNConfig.ENERGY_CAPACITY.get() : clientEnergyCapacity;
            }

            @Override
            public void set(int pValue) {
                clientEnergyCapacity = pValue;
            }
        });
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public int getClientSpeed() {
        return speedSlot.get();
    }

    public int getClientCount() {
        return countSlot.get();
    }

    public int getClientEnergyStored() {
        return energyStoredSlot.get();
    }

    public int getClientEnergyCapacity() {
        return energyCapacitySlot.get();
    }

    public void adjustSpeed(int delta) {
        if (blockEntity != null) {
            if (!RNConfig.PRECISE_TICK_ADJUST.get()) {
                delta = delta * 20;
            }
            blockEntity.adjustSpeed(delta);
        }
    }

    public void adjustCount(int delta) {
        if (blockEntity != null) {
            blockEntity.adjustCount(delta);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack = slot.getItem();
            result = itemStack.copy();

            if (index < SLOT_COUNT) {
                if (!this.moveItemStackTo(itemStack, SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (this.slots.getFirst().hasItem()) {
                    return ItemStack.EMPTY;
                }

                // 尝试放入 1 个物品
                ItemStack singleItem = itemStack.copyWithCount(1);
                if (this.moveItemStackTo(singleItem, 0, SLOT_COUNT, false)) {
                    itemStack.shrink(1);
                    if (itemStack.isEmpty()) {
                        slot.set(ItemStack.EMPTY);
                    } else {
                        slot.setChanged();
                    }
                    return result;
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (itemStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }
}