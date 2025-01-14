/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.lib.engine;

import buildcraft.api.enums.EnumPowerStage;
import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import buildcraft.api.mj.MjCapabilityHelper;
import buildcraft.api.tiles.IDebuggable;
import buildcraft.api.tiles.ITickable;
import buildcraft.lib.block.VanillaRotationHandlers;
import buildcraft.lib.misc.LocaleUtil;
import buildcraft.lib.misc.NBTUtilBC;
import buildcraft.lib.misc.collect.OrderedEnumMap;
import buildcraft.lib.misc.data.ModelVariableData;
import buildcraft.lib.net.PacketBufferBC;
import buildcraft.lib.tile.TileBC_Neptune;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

public abstract class TileEngineBase_BC8 extends TileBC_Neptune implements ITickable, IDebuggable {

    /** Heat per {@link MjAPI#MJ}. */
    public static final double HEAT_PER_MJ = 0.0023;

    public static final double MIN_HEAT = 20;
    public static final double IDEAL_HEAT = 100;
    public static final double MAX_HEAT = 250;

    @Nonnull
    public final IMjConnector mjConnector = createConnector();
    private final MjCapabilityHelper mjCaps = new MjCapabilityHelper(mjConnector);

    protected double heat = MIN_HEAT;// TODO: sync gui data
    protected long power = 0;// TODO: sync gui data
    private long lastPower = 0;
    /** Increments from 0 to 1. Above 0.5 all of the held power is emitted. */
    private float progress, lastProgress;
    private int progressPart = 0;

    protected EnumPowerStage powerStage = EnumPowerStage.BLUE;
    protected Direction currentDirection = Direction.UP;

    public long currentOutput; // TODO: sync gui data
    public boolean isRedstonePowered = false;
    protected boolean isPumping = false;

    /** The model variables, used to keep track of the various state-based variables. */
    public final ModelVariableData clientModelData = new ModelVariableData();

    // Needed: Power stored

    public TileEngineBase_BC8(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        currentDirection = NBTUtilBC.readEnum(nbt.get("currentDirection"), Direction.class);
        if (currentDirection == null) {
            currentDirection = Direction.UP;
        }
        isRedstonePowered = nbt.getBoolean("isRedstonePowered");
        heat = nbt.getDouble("heat");
        power = nbt.getLong("power");
        progress = nbt.getFloat("progress");
        progressPart = nbt.getInt("progressPart");
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("currentDirection", NBTUtilBC.writeEnum(currentDirection));
        nbt.putBoolean("isRedstonePowered", isRedstonePowered);
        nbt.putDouble("heat", heat);
        nbt.putLong("power", power);
        nbt.putFloat("progress", progress);
        nbt.putInt("progressPart", progressPart);
    }

    @Override
    public void readPayload(int id, PacketBufferBC buffer, NetworkDirection side, NetworkEvent.Context ctx) throws IOException {
        super.readPayload(id, buffer, side, ctx);
        if (side == NetworkDirection.PLAY_TO_CLIENT) {
            if (id == NET_RENDER_DATA) {
                isPumping = buffer.readBoolean();
                currentDirection = buffer.readEnum(Direction.class);
                powerStage = buffer.readEnum(EnumPowerStage.class);
                progress = buffer.readFloat();
            } else if (id == NET_GUI_DATA) {
                heat = buffer.readFloat();
                currentOutput = buffer.readLong();
                power = buffer.readLong();
            } else if (id == NET_GUI_TICK) {
                heat = buffer.readFloat();
                currentOutput = buffer.readLong();
                power = buffer.readLong();
            }
        }
    }

    @Override
    public void writePayload(int id, PacketBufferBC buffer, Dist side) {
        super.writePayload(id, buffer, side);
        if (side == Dist.DEDICATED_SERVER) {
            if (id == NET_RENDER_DATA) {
                buffer.writeBoolean(isPumping);
                buffer.writeEnum(currentDirection);
                buffer.writeEnum(powerStage);
                buffer.writeFloat(progress);
            } else if (id == NET_GUI_DATA) {
                buffer.writeFloat((float) heat);
                buffer.writeLong(currentOutput);
                buffer.writeLong(power);
            } else if (id == NET_GUI_TICK) {
                buffer.writeFloat((float) heat);
                buffer.writeLong(currentOutput);
                buffer.writeLong(power);
            }
        }
    }

    public InteractionResult attemptRotation() {
        OrderedEnumMap<Direction> possible = VanillaRotationHandlers.ROTATE_FACING;
        Direction current = currentDirection;
        for (int i = 0; i < 6; i++) {
            current = possible.next(current);
            if (isFacingReceiver(current)) {
                if (currentDirection != current) {
                    currentDirection = current;
                    // makeTileCache();
                    sendNetworkUpdate(NET_RENDER_DATA);
                    redrawBlock();
                    // TODO Calen notifyNeighborsRespectDebug???
//                    world.notifyNeighborsRespectDebug(getPos(), getBlockType(), true);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            }
        }
        return InteractionResult.PASS;
    }

    private boolean isFacingReceiver(Direction dir) {
        return getReceiverToPower(dir) != null;
    }

    protected final boolean canChain() {
        return getMaxChainLength() > 0;
    }

    /** @return The number of additional engines that this engine can send power through. */
    protected int getMaxChainLength() {
        return 2;
    }

    public void rotateIfInvalid() {
        if (currentDirection != null && isFacingReceiver(currentDirection)) {
            return;
        }
        attemptRotation();
        if (currentDirection == null) {
            currentDirection = Direction.UP;
        }
    }

    @Override
    public void onPlacedBy(LivingEntity placer, ItemStack stack) {
        super.onPlacedBy(placer, stack);
        currentDirection = null; // Force rotateIfInvalid to always attempt to rotate
        rotateIfInvalid();
    }

    protected Biome getBiome() {
        // TODO: Cache this!
        return level.getBiome(getBlockPos()).value();
    }

    /** @return The heat of the current biome, in celsius. */
    protected float getBiomeHeat() {
        Biome biome = getBiome();
        float temp = biome.getTemperature(getBlockPos());
        return Math.max(0, Math.min(30, temp * 15f));
    }

    public double getPowerLevel() {
        return power / (double) getMaxPower();
    }

    protected EnumPowerStage computePowerStage() {
        double heatLevel = getHeatLevel();
        if (heatLevel < 0.25f) return EnumPowerStage.BLUE;
        else if (heatLevel < 0.5f) return EnumPowerStage.GREEN;
        else if (heatLevel < 0.75f) return EnumPowerStage.YELLOW;
        else if (heatLevel < 0.85f) return EnumPowerStage.RED;
        else return EnumPowerStage.OVERHEAT;
    }

    public final EnumPowerStage getPowerStage() {
        if (!level.isClientSide) {
            EnumPowerStage newStage = computePowerStage();

            if (powerStage != newStage) {
                powerStage = newStage;
                sendNetworkUpdate(NET_RENDER_DATA);
            }
        }

        return powerStage;
    }

    public void updateHeatLevel() {
        heat = ((MAX_HEAT - MIN_HEAT) * getPowerLevel()) + MIN_HEAT;
    }

    public double getHeatLevel() {
        return (heat - MIN_HEAT) / (MAX_HEAT - MIN_HEAT);
    }

    public double getIdealHeatLevel() {
        return heat / IDEAL_HEAT;
    }

    public double getHeat() {
        return heat;
    }

    public double getPistonSpeed() {
        switch (getPowerStage()) {
            case BLUE:
                return 0.02;
            case GREEN:
                return 0.04;
            case YELLOW:
                return 0.08;
            case RED:
                return 0.12;
            default:
                return 0;
        }
    }

    @Nonnull
    protected abstract IMjConnector createConnector();

    @Override
    public void onNeighbourBlockChanged(Block block, BlockPos nehighbour) {
        super.onNeighbourBlockChanged(block, nehighbour);
//        isRedstonePowered = world.isBlockIndirectlyGettingPowered(getPos()) > 0;
        isRedstonePowered = level.hasNeighborSignal(getBlockPos());
    }

    @Override
    public void update() {
        ITickable.super.update();
        deltaManager.tick();
        if (cannotUpdate()) return;

        boolean overheat = getPowerStage() == EnumPowerStage.OVERHEAT;

        if (level.isClientSide) {
            lastProgress = progress;

            if (isPumping) {
                progress += getPistonSpeed();

                if (progress >= 1) {
                    progress = 0;
                }
            } else if (progress > 0) {
                progress -= 0.01f;
            }
            clientModelData.tick();
            return;
        }

        lastPower = 0;

        if (!isRedstonePowered) {
            if (power > MjAPI.MJ) {
                power -= MjAPI.MJ;
            } else if (power > 0) {
                power = 0;
            }
        }

        updateHeatLevel();
        getPowerStage();
        engineUpdate();

        if (progressPart != 0) {
            progress += getPistonSpeed();

            if (progress > 0.5 && progressPart == 1) {
                progressPart = 2;
                sendPower(); // Comment out for constant power
            } else if (progress >= 1) {
                progress = 0;
                progressPart = 0;
            }
        } else if (isRedstonePowered && isActive()) {
            if (getPowerToExtract(false) > 0) {
                progressPart = 1;
                setPumping(true);
            } else {
                setPumping(false);
            }
        } else {
            setPumping(false);
        }

        // Uncomment for constant power
        // if (isRedstonePowered && isActive()) {
        // sendPower();
        // } else currentOutput = 0;

        if (!overheat) {
            burn();
        }

        markChunkDirty();
    }

    private long getPowerToExtract(boolean doExtract) {
        IMjReceiver receiver = getReceiverToPower(currentDirection);
        if (receiver == null) {
            return 0;
        }

        // Pulsed power
        return extractPower(0, receiver.getPowerRequested(), doExtract);
        // TODO: Use this:
        // return extractPower(receiver.getMinPowerReceived(), receiver.getMaxPowerReceived(), false);

        // Constant power
        // return extractEnergy(0, getActualOutput(), false); // Uncomment for constant power
    }

    private void sendPower() {
        IMjReceiver receiver = getReceiverToPower(currentDirection);
        if (receiver != null) {
            long extracted = getPowerToExtract(true);
            if (extracted > 0) {
                long excess = receiver.receivePower(extracted, false);
                extractPower(extracted - excess, extracted - excess, true); // Comment out for constant power
                // currentOutput = extractEnergy(0, needed, true); // Uncomment for constant power
            }
        }
    }

    // Uncomment out for constant power
    // public float getActualOutput() {
    // float heatLevel = getIdealHeatLevel();
    // return getCurrentOutput() * heatLevel;
    // }
    protected void burn() {
    }

    protected void engineUpdate() {
        if (!isRedstonePowered) {
            if (power >= 1) {
                power -= 1;
            } else if (power < 1) {
                power = 0;
            }
        }
    }

    public boolean isActive() {
        return true;
    }

    protected final void setPumping(boolean isActive) {
        if (this.isPumping == isActive) {
            return;
        }

        this.isPumping = isActive;
        sendNetworkUpdate(NET_RENDER_DATA);
    }

    // TEMP
    @FunctionalInterface
    public interface ITileBuffer {
        BlockEntity getTile();
    }

    /** Temp! This should be replaced with a tile buffer! */
    public ITileBuffer getTileBuffer(Direction side) {
        BlockEntity tile = level.getBlockEntity(getBlockPos().relative(side));
        return () -> tile;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        // tileCache = null;
        // checkOrientation = true;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        // tileCache = null;
        // checkOrientation = true;
    }

    /* STATE INFORMATION */
    public abstract boolean isBurning();

    // IPowerReceptor stuffs -- move!
    // @Override
    // public PowerReceiver getPowerReceiver(ForgeDirection side) {
    // return powerHandler.getPowerReceiver();
    // }
    //
    // @Override
    // public void doWork(PowerHandler workProvider) {
    // if (worldObj.isRemote) {
    // return;
    // }
    //
    // addEnergy(powerHandler.useEnergy(1, maxEnergyReceived(), true) * 0.95F);
    // }

    public void addPower(long microJoules) {
        power += microJoules;
        lastPower += microJoules;

        if (getPowerStage() == EnumPowerStage.OVERHEAT) {
            // TODO: turn engine off
            // worldObj.createExplosion(null, xCoord, yCoord, zCoord, explosionRange(), true);
            // worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        }

        if (power > getMaxPower()) {
            power = getMaxPower();
        }
    }

    public long extractPower(long min, long max, boolean doExtract) {
        if (power < min) {
            return 0;
        }

        long actualMax;

        if (max > maxPowerExtracted()) {
            actualMax = maxPowerExtracted();
        } else {
            actualMax = max;
        }

        if (actualMax < min) {
            return 0;
        }

        long extracted;

        if (power >= actualMax) {
            extracted = actualMax;

            if (doExtract) {
                power -= actualMax;
            }
        } else {
            extracted = power;

            if (doExtract) {
                power = 0;
            }
        }

        return extracted;
    }

    public final boolean isPoweredTile(BlockEntity tile, Direction side) {
        if (tile == null) return false;
        if (tile.getClass() == getClass()) {
            TileEngineBase_BC8 other = (TileEngineBase_BC8) tile;
            return other.currentDirection == currentDirection;
        }
        return getReceiverToPower(tile, side) != null;
    }

    /** @deprecated Replaced with {@link #getReceiverToPower(Direction)}. */
    @Deprecated
    public IMjReceiver getReceiverToPower(BlockEntity tile, Direction side) {
        if (tile == null) return null;
        IMjReceiver rec = tile.getCapability(MjAPI.CAP_RECEIVER, side.getOpposite()).orElse(null);
        if (rec != null && rec.canConnect(mjConnector) && mjConnector.canConnect(rec)) {
            return rec;
        } else {
            return null;
        }
    }

    public IMjReceiver getReceiverToPower(Direction side) {
        TileEngineBase_BC8 engine = this;
        BlockEntity next = null;

        for (int len = 0; len <= getMaxChainLength(); len++) {
            next = engine.getTileBuffer(side).getTile();

            if (next == null) {
                return null;
            }

            if (next.getClass() == getClass()) {
                if (side != ((TileEngineBase_BC8) next).currentDirection) {
                    return null;
                }
            }

            if (next instanceof TileEngineBase_BC8) {
                if (next.getClass() != getClass()) {
                    return null;
                }
                engine = (TileEngineBase_BC8) next;
            } else {
                break;
            }
        }

        if (next == null || next instanceof TileEngineBase_BC8) {
            return null;
        }

        IMjReceiver recv = next.getCapability(MjAPI.CAP_RECEIVER, side.getOpposite()).orElse(null);
        if (recv != null && recv.canConnect(mjConnector) && mjConnector.canConnect(recv)) {
            return recv;
        } else {
            return null;
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
        if (facing == currentDirection) {
            return mjCaps.getCapability(capability, facing);
        } else {
            return super.getCapability(capability, facing);
        }
    }

    public abstract long getMaxPower();

    public long minPowerReceived() {
        return 2 * MjAPI.MJ;
    }

    public abstract long maxPowerReceived();

    public abstract long maxPowerExtracted();

    public abstract float explosionRange();

    public long getEnergyStored() {
        return power;
    }

    public abstract long getCurrentOutput();

    public boolean isEngineOn() {
        return isPumping;
    }

    @OnlyIn(Dist.CLIENT)
    public float getProgressClient(float partialTicks) {
        float last = lastProgress;
        float now = progress;
        if (last > 0.5 && now < 0.5) {
            // we just returned
            now += 1;
        }
        float interp = last * (1 - partialTicks) + now * partialTicks;
        return interp % 1;
    }

    public Direction getCurrentFacing() {
        return currentDirection;
    }

    @Override
//    public void getDebugInfo(List<String> left, List<String> right, Direction side)
    public void getDebugInfo(List<Component> left, List<Component> right, Direction side) {
//        left.add("facing = " + currentDirection);
//        left.add("heat = " + LocaleUtil.localizeHeat(heat) + " -- " + String.format("%.2f %%", getHeatLevel()));
//        left.add("power = " + LocaleUtil.localizeMj(power));
//        left.add("stage = " + powerStage);
//        left.add("progress = " + progress);
//        left.add("last = " + LocaleUtil.localizeMjFlow(lastPower));
        left.add(Component.literal("facing = " + currentDirection));
        left.add(Component.literal("heat = ").append(Component.literal(LocaleUtil.localizeHeat(heat) + " -- " + String.format("%.2f %%", getHeatLevel()))));
        left.add(Component.literal("power = ").append(LocaleUtil.localizeMjComponent(power)));
        left.add(Component.literal("stage = " + powerStage));
        left.add(Component.literal("progress = " + progress));
        left.add(Component.literal("last = ").append(LocaleUtil.localizeMjFlow(lastPower)));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void getClientDebugInfo(List<String> left, List<String> right, Direction side) {
        left.add("Current Model Variables:");
        clientModelData.addDebugInfo(left);
    }

//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public boolean hasFastRenderer() {
//        return true;
//    }
}
