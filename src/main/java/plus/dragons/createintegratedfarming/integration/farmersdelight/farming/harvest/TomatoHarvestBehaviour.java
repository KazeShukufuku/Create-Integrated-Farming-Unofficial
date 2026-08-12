/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.farmersdelight.farming.harvest;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import vectorwing.farmersdelight.common.block.HangingTomatoBlock;
import vectorwing.farmersdelight.common.block.TomatoBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModSounds;

public class TomatoHarvestBehaviour implements CustomHarvestBehaviour {
    private final TomatoBlock tomato;

    private TomatoHarvestBehaviour(TomatoBlock tomato) {
        this.tomato = tomato;
    }

    public static @Nullable TomatoHarvestBehaviour create(Block block) {
        return block instanceof TomatoBlock tomato ? new TomatoHarvestBehaviour(tomato) : null;
    }

    @Override
    public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        boolean mature = tomato.getAge(state) == tomato.getMaxAge();
        if (!mature && !CustomHarvestBehaviour.partial())
            return;
        Level level = context.world;
        if (!CustomHarvestBehaviour.replant()) {
            breakTomatoes(level, behaviour, context, pos, state);
            return;
        }
        if (mature) {
            dropTomatoes(level, behaviour, context);
            level.playSound(null, pos, ModSounds.BLOCK_TOMATOES_PICK_TOMATOES.get(), SoundSource.BLOCKS,
                    1.0F, 0.8F + level.random.nextFloat() * 0.4F);
        }
        level.setBlock(pos, state.setValue(tomato.getAgeProperty(), 0), 2);
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        boolean mature = tomato.getAge(state) == tomato.getMaxAge();
        if (!mature && !context.harvestPartiallyGrown())
            return false;
        Level level = context.level();
        if (!context.replant()) {
            breakTomatoes(context, pos, state);
            return true;
        }
        if (mature) {
            context.collect(new ItemStack(ModItems.TOMATO.get(), 1 + level.random.nextInt(2)));
            if (level.random.nextFloat() < 0.05F)
                context.collect(new ItemStack(ModItems.ROTTEN_TOMATO.get()));
            level.playSound(null, pos, ModSounds.BLOCK_TOMATOES_PICK_TOMATOES.get(), SoundSource.BLOCKS,
                    1.0F, 0.8F + level.random.nextFloat() * 0.4F);
        }
        level.setBlock(pos, state.setValue(tomato.getAgeProperty(), 0), 2);
        return true;
    }

    private void breakTomatoes(AreaHarvestContext context, BlockPos pos, BlockState state) {
        BlockState above = context.level().getBlockState(pos.above());
        if (above.getBlock() instanceof TomatoBlock)
            breakTomatoes(context, pos.above(), above);
        boolean restoreRope = shouldRestoreRope(state);
        CustomHarvestBehaviour.harvestBlock(context.level(), pos,
                net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), null,
                context.tool(), 1.0F, context::collect);
        if (restoreRope)
            restoreRope(context.level(), pos, state);
    }

    private void breakTomatoes(Level level, HarvesterMovementBehaviour behaviour, MovementContext context,
            BlockPos pos, BlockState state) {
        BlockState above = level.getBlockState(pos.above());
        if (above.getBlock() instanceof TomatoBlock)
            breakTomatoes(level, behaviour, context, pos.above(), above);
        boolean restoreRope = shouldRestoreRope(state);
        BlockHelper.destroyBlockAs(level, pos, null, CustomHarvestBehaviour.getHarvestTool(context), 1.0F,
                stack -> behaviour.dropItem(context, stack));
        if (restoreRope)
            restoreRope(level, pos, state);
    }

    private void dropTomatoes(Level level, HarvesterMovementBehaviour behaviour, MovementContext context) {
        behaviour.dropItem(context, new ItemStack(ModItems.TOMATO.get(), 1 + level.random.nextInt(2)));
        if (level.random.nextFloat() < 0.05F)
            behaviour.dropItem(context, new ItemStack(ModItems.ROTTEN_TOMATO.get()));
    }

    private static boolean shouldRestoreRope(BlockState state) {
        return state.getBlock() instanceof HangingTomatoBlock
                || state.hasProperty(TomatoBlock.ROPELOGGED) && state.getValue(TomatoBlock.ROPELOGGED);
    }

    private static void restoreRope(Level level, BlockPos pos, BlockState state) {
        HangingTomatoBlock ropePlacer = state.getBlock() instanceof HangingTomatoBlock hangingTomato
                ? hangingTomato : (HangingTomatoBlock) ModBlocks.TOMATO_CROP_ON_ROPE.get();
        ropePlacer.placeRope(level, pos);
    }
}
