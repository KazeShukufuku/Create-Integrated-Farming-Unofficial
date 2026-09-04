/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.common.fishing.net;

import com.simibubi.create.AllShapes;
import com.simibubi.create.api.schematic.state.SchematicStateFilter;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import java.util.List;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragonsplus.common.fluids.WaterAndLavaLoggedBlock;

public class LavaFishingNetBlock extends WrenchableDirectionalBlock
        implements WaterAndLavaLoggedBlock, SchematicStateFilter {
    protected static final int PLACEMENT_HELPER_ID = PlacementHelpers.register(new PlacementHelper());

    public LavaFishingNetBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.UP)
                .setValue(FLUID, ContainedFluid.EMPTY));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        IPlacementHelper placementHelper = PlacementHelpers.get(PLACEMENT_HELPER_ID);
        if (!player.isShiftKeyDown() && player.mayBuild() && placementHelper.matchesItem(stack)) {
            placementHelper.getOffset(player, level, state, pos, hitResult)
                    .placeInWorld(level, (BlockItem) stack.getItem(), player, hand, hitResult);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AllShapes.SAIL.get(state.getValue(FACING));
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity living && FishingNetEntityCaptures.canCapture(living))
            entity.makeStuckInBlock(state, new Vec3(0.25, 0.05, 0.25));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FLUID));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        return withFluid(state, context);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.isClientSide || state.getBlock() == oldState.getBlock())
            return;
        BlockState refreshed = state.setValue(FLUID, ContainedFluid.EMPTY);
        FluidState oldFluid = oldState.getFluidState();
        if (oldFluid.getType() == Fluids.WATER)
            refreshed = refreshed.setValue(FLUID, ContainedFluid.WATER);
        else if (oldFluid.getType() == Fluids.LAVA)
            refreshed = refreshed.setValue(FLUID, ContainedFluid.LAVA);
        if (refreshed != state)
            level.setBlock(pos, refreshed, Block.UPDATE_ALL);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (level.isClientSide || state.getBlock() == newState.getBlock())
            return;
        FluidState fluid = state.getFluidState();
        if (!fluid.isEmpty() && newState.getFluidState().isEmpty())
            level.setBlock(pos, fluid.createLegacyBlock(), Block.UPDATE_ALL);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return fluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        updateFluid(level, state, pos);
        return state;
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity.isSuppressingBounce())
            super.fallOn(level, state, pos, entity, fallDistance);
        else
            entity.causeFallDamage(fallDistance, 0.5F, level.damageSources().fall());
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        if (entity.isSuppressingBounce())
            super.updateEntityAfterFallOn(level, entity);
        else {
            Vec3 movement = entity.getDeltaMovement();
            if (movement.y < 0.0) {
                double weight = entity instanceof LivingEntity ? 0.3 : 0.4;
                entity.setDeltaMovement(movement.x, -movement.y * weight, movement.z);
            }
        }
    }

    @Override
    public BlockState filterStates(@Nullable BlockEntity blockEntity, BlockState state) {
        return state.setValue(FLUID, ContainedFluid.EMPTY);
    }

    protected static class PlacementHelper implements IPlacementHelper {
        @Override
        public Predicate<ItemStack> getItemPredicate() {
            return stack -> stack.getItem() instanceof BlockItem item
                    && item.getBlock() instanceof LavaFishingNetBlock;
        }

        @Override
        public Predicate<BlockState> getStatePredicate() {
            return state -> state.getBlock() instanceof LavaFishingNetBlock;
        }

        @Override
        public PlacementOffset getOffset(Player player, Level level, BlockState state,
                BlockPos pos, BlockHitResult hitResult) {
            List<Direction> directions = IPlacementHelper.orderedByDistanceExceptAxis(
                    pos, hitResult.getLocation(), state.getValue(FACING).getAxis(),
                    direction -> level.getBlockState(pos.relative(direction)).canBeReplaced());
            if (directions.isEmpty())
                return PlacementOffset.fail();
            Direction direction = directions.get(0);
            return PlacementOffset.success(pos.relative(direction), placed -> {
                FluidState fluid = level.getFluidState(pos.relative(direction));
                BlockState result = placed.setValue(FACING, state.getValue(FACING));
                if (fluid.getType() == Fluids.WATER)
                    return result.setValue(FLUID, ContainedFluid.WATER);
                if (fluid.getType() == Fluids.LAVA)
                    return result.setValue(FLUID, ContainedFluid.LAVA);
                return result;
            });
        }
    }
}
