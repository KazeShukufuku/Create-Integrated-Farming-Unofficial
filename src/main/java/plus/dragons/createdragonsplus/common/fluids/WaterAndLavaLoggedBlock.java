/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createdragonsplus.common.fluids;

import java.util.Locale;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public interface WaterAndLavaLoggedBlock extends BucketPickup, LiquidBlockContainer {
    EnumProperty<ContainedFluid> FLUID = EnumProperty.create("fluid", ContainedFluid.class);

    @Override
    default boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return fluid == Fluids.WATER || fluid == Fluids.LAVA;
    }

    @Override
    default boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        ContainedFluid containedFluid = state.getValue(FLUID);
        if (containedFluid != ContainedFluid.EMPTY) {
            return false;
        }
        Fluid placedFluid = fluidState.getType();
        if (placedFluid == Fluids.WATER || placedFluid == Fluids.LAVA) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(FLUID, placedFluid == Fluids.WATER ? ContainedFluid.WATER : ContainedFluid.LAVA), 3);
                level.scheduleTick(pos, placedFluid, placedFluid.getTickDelay(level));
            }
            return true;
        }
        return false;
    }

    @Override
    default ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        ContainedFluid containedFluid = state.getValue(FLUID);
        if (containedFluid == ContainedFluid.EMPTY) {
            return ItemStack.EMPTY;
        }
        level.setBlock(pos, state.setValue(FLUID, ContainedFluid.EMPTY), 3);
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
        return containedFluid == ContainedFluid.WATER ? new ItemStack(Items.WATER_BUCKET) : new ItemStack(Items.LAVA_BUCKET);
    }

    @Override
    default Optional<SoundEvent> getPickupSound() {
        return Optional.empty();
    }

    default Optional<SoundEvent> getPickupSound(BlockState state) {
        return switch (state.getValue(FLUID)) {
            case EMPTY -> Optional.empty();
            case WATER -> Fluids.WATER.getPickupSound();
            case LAVA -> Fluids.LAVA.getPickupSound();
        };
    }

    default FluidState fluidState(BlockState state) {
        return switch (state.getValue(FLUID)) {
            case EMPTY -> Fluids.EMPTY.defaultFluidState();
            case WATER -> Fluids.WATER.getSource(false);
            case LAVA -> Fluids.LAVA.getSource(false);
        };
    }

    default void updateFluid(LevelAccessor level, BlockState state, BlockPos pos) {
        switch (state.getValue(FLUID)) {
            case WATER -> level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            case LAVA -> level.scheduleTick(pos, Fluids.LAVA, Fluids.LAVA.getTickDelay(level));
            default -> {
            }
        }
    }

    default BlockState withFluid(BlockState placementState, BlockPlaceContext context) {
        return withFluid(context.getLevel(), placementState, context.getClickedPos());
    }

    static BlockState withFluid(LevelAccessor level, BlockState placementState, BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos);
        if (placementState.isAir()) {
            return fluidState.isEmpty() ? placementState : fluidState.createLegacyBlock();
        }
        if (!(placementState.getBlock() instanceof WaterAndLavaLoggedBlock)) {
            return placementState;
        }
        ContainedFluid containedFluid = ContainedFluid.EMPTY;
        if (fluidState.getType() == Fluids.WATER) {
            containedFluid = ContainedFluid.WATER;
        } else if (fluidState.getType() == Fluids.LAVA) {
            containedFluid = ContainedFluid.LAVA;
        }
        return placementState.setValue(FLUID, containedFluid);
    }

    enum ContainedFluid implements StringRepresentable {
        EMPTY,
        WATER,
        LAVA;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
