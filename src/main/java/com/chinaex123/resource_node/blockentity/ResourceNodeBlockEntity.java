package com.chinaex123.resource_node.blockentity;

import com.chinaex123.resource_node.client.menu.ResourceNodeMenu;
import com.chinaex123.resource_node.config.RNConfig;
import com.chinaex123.resource_node.init.RNBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResourceNodeBlockEntity extends BlockEntity implements MenuProvider {

    public static final String NBT_INVENTORY = "Inventory";
    public static final String NBT_SPEED = "SpeedInterval";
    public static final String NBT_COUNT = "OutputCount";
    public static final String NBT_TIMER = "TickTimer";
    public static final String NBT_ENERGY = "EnergyStored";

    /** 生成间隔（tick） */
    private int speedInterval = 20;
    /** 每次生成的物品数量 */
    private int outputCount = 1;
    /** 内部计时器 */
    private int tickTimer = 0;
    /** 当前存储的 FE 能量 */
    private int energyStored = 0;

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (getLevel() != null && !getLevel().isClientSide) {
                getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public final IEnergyStorage energyHandler = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int capacity = RNConfig.ENERGY_CAPACITY.get();
            int canReceive = Math.min(maxReceive, capacity - energyStored);
            if (!simulate) {
                energyStored += canReceive;
                setChanged();
            }
            return canReceive;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int canExtract = Math.min(maxExtract, energyStored);
            if (!simulate) {
                energyStored -= canExtract;
                setChanged();
            }
            return canExtract;
        }

        @Override
        public int getEnergyStored() {
            return energyStored;
        }

        @Override
        public int getMaxEnergyStored() {
            return RNConfig.ENERGY_CAPACITY.get();
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    };

    public ResourceNodeBlockEntity(BlockPos pos, BlockState state) {
        super(RNBlockEntities.RESOURCE_NODE.get(), pos, state);
    }

    public ResourceNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int getTickTimer() {
        return tickTimer;
    }

    public int getSpeedInterval() {
        return speedInterval;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public int getEnergyStored() {
        return energyStored;
    }

    public int getEnergyCost() {
        if (!RNConfig.ENERGY_ENABLED.get()) return 0;

        int minCost = RNConfig.ENERGY_MIN_COST.get();
        int maxCost = RNConfig.ENERGY_MAX_COST.get();
        if (minCost >= maxCost) return maxCost;

        int minSpeed = RNConfig.MIN_SPEED.get();
        int maxSpeed = RNConfig.MAX_SPEED.get();
        int minCount = RNConfig.MIN_COUNT.get();
        int maxCount = RNConfig.MAX_COUNT.get();

        // 速度越快值越大，数量越多值越大
        double speedRatio = (maxSpeed == minSpeed) ? 0 : (double) (maxSpeed - speedInterval) / (maxSpeed - minSpeed);
        double countRatio = (maxCount == minCount) ? 0 : (double) (outputCount - minCount) / (maxCount - minCount);

        // 综合因子 [0, 2]，用指数插值
        double combined = speedRatio + countRatio;
        double maxCombined = 2.0;

        // 指数插值: cost = minCost * (maxCost/minCost)^(combined/maxCombined)
        double ratio = (double) maxCost / minCost;
        double cost = minCost * Math.pow(ratio, combined / maxCombined);

        return (int) Math.round(cost);
    }

    public void setSpeedInterval(int v) {
        this.speedInterval = Math.clamp(v, RNConfig.MIN_SPEED.get(), RNConfig.MAX_SPEED.get());
        if (this.tickTimer >= this.speedInterval) {
            this.tickTimer = 0;
        }
        setChanged();
    }

    public void setOutputCount(int v) {
        this.outputCount = Math.clamp(v, RNConfig.MIN_COUNT.get(), RNConfig.MAX_COUNT.get());
        setChanged();
    }

    public void adjustSpeed(int delta) {
        int minAllowedSpeed = RNConfig.PRECISE_TICK_ADJUST.get() ? RNConfig.MIN_SPEED.get() : 20;
        int newSpeed = this.speedInterval + delta;

        if (newSpeed < minAllowedSpeed) {
            newSpeed = minAllowedSpeed;
        }

        setSpeedInterval(newSpeed);
    }

    public void adjustCount(int delta) {
        setOutputCount(this.outputCount + delta);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ResourceNodeBlockEntity be) {
        ItemStack template = be.inventory.getStackInSlot(0);
        if (template.isEmpty()) return;

        be.tickTimer++;

        if (level.isClientSide) {
            if (be.tickTimer >= be.speedInterval) {
                be.tickTimer = 0;
            }
            return;
        }

        if (be.tickTimer >= be.speedInterval) {
            int cost = be.getEnergyCost();
            if (be.energyStored >= cost) {
                be.energyStored -= cost;
                be.tickTimer = 0;
                generateItems(level, pos, template, be.outputCount);
                be.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    private static void generateItems(Level level, BlockPos pos, ItemStack template, int count) {
        // 生成产生的音效
        level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP,
                SoundSource.BLOCKS, 0.5f, 0.9f + level.random.nextFloat() * 0.2f);

        // 生成产生的粒子
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 1.0;
        double cz = pos.getZ() + 0.5;
        ((ServerLevel) level).sendParticles(
                ParticleTypes.HAPPY_VILLAGER,
                cx + (level.random.nextDouble() - 0.5) * 0.6,
                cy + (level.random.nextDouble() - 0.5) * 0.3,
                cz + (level.random.nextDouble() - 0.5) * 0.6,
                1, 0, 0.02, 0, 0);

        int remaining = count;
        int maxStack = template.getMaxStackSize();

        while (remaining > 0) {
            int stackSize = Math.min(remaining, maxStack);
            ItemStack drop = template.copy();
            drop.setCount(stackSize);

            Vec3 spawnPos;
            boolean zeroVelocity;

            switch (RNConfig.SPAWN_MODE.get()) {
                case DROP -> {
                    // 从上方方块中心掉落
                    spawnPos = Vec3.atCenterOf(pos.above())
                            .add((level.random.nextDouble() - 0.5) * 0.3, 0.1, (level.random.nextDouble() - 0.5) * 0.3);
                    zeroVelocity = false;
                }
                case SCATTER -> {
                    // 顶面随机分散生成
                    spawnPos = new Vec3(
                            pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.3,
                            pos.getY() + 1.0,
                            pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.3);
                    zeroVelocity = false;
                }
                case CENTER -> {
                    // 顶面正中心生成
                    spawnPos = new Vec3(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                    zeroVelocity = true;
                }
                default -> throw new IllegalStateException("Unknown SpawnMode: " + RNConfig.SPAWN_MODE.get());
            }

            ItemEntity entity = new ItemEntity(level, spawnPos.x, spawnPos.y, spawnPos.z, drop);
            if (zeroVelocity) {
                entity.setDeltaMovement(Vec3.ZERO);
            }
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);

            remaining -= stackSize;
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.resource_node.resource_node");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ResourceNodeMenu(
                containerId,
                playerInventory,
                inventory,
                this.worldPosition,
                this
        );
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(NBT_INVENTORY, inventory.serializeNBT(registries));
        tag.putInt(NBT_SPEED, speedInterval);
        tag.putInt(NBT_COUNT, outputCount);
        tag.putInt(NBT_TIMER, tickTimer);
        tag.putInt(NBT_ENERGY, energyStored);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put(NBT_INVENTORY, inventory.serializeNBT(registries));
        tag.putInt(NBT_SPEED, speedInterval);
        tag.putInt(NBT_COUNT, outputCount);
        tag.putInt(NBT_TIMER, tickTimer);
        tag.putInt(NBT_ENERGY, energyStored);
        return tag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(NBT_INVENTORY)) {
            inventory.deserializeNBT(registries, tag.getCompound(NBT_INVENTORY));
        }
        speedInterval = tag.getInt(NBT_SPEED);
        outputCount = tag.getInt(NBT_COUNT);
        tickTimer = tag.getInt(NBT_TIMER);
        energyStored = tag.getInt(NBT_ENERGY);
        if (speedInterval < RNConfig.MIN_SPEED.get() || speedInterval > RNConfig.MAX_SPEED.get()) speedInterval = 20;
        if (outputCount < RNConfig.MIN_COUNT.get() || outputCount > RNConfig.MAX_COUNT.get()) outputCount = 1;
    }
}