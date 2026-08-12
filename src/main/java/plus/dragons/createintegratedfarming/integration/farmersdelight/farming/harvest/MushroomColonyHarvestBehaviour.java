/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.farmersdelight.farming.harvest;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.config.CIFConfig;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;

public class MushroomColonyHarvestBehaviour implements CustomHarvestBehaviour {
    private final MushroomColonyBlock colony;
    private final Block mushroom;

    private MushroomColonyHarvestBehaviour(MushroomColonyBlock colony, Block mushroom) {
        this.colony = colony;
        this.mushroom = mushroom;
    }

    public static @Nullable MushroomColonyHarvestBehaviour create(Block block) {
        if (!(block instanceof MushroomColonyBlock colony)
                || !(colony.mushroomType.get() instanceof BlockItem mushroom)
                || BuiltInRegistries.BLOCK.getKey(colony).getPath().contains("cloudshroom"))
            return null;
        return new MushroomColonyHarvestBehaviour(colony, mushroom.getBlock());
    }

    @Override
    public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        if (CIFConfig.server().mushroomColoniesDropSelf.get())
            harvestColony(behaviour, context, pos, state);
        else
            harvestMushroom(behaviour, context, pos, state);
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        int age = state.getValue(colony.getAgeProperty());
        if (age == 0 || age < colony.getMaxAge() && !context.harvestPartiallyGrown())
            return false;
        if (!CIFConfig.server().mushroomColoniesDropSelf.get()) {
            context.level().playSound(null, pos, SoundEvents.MOOSHROOM_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            context.level().setBlockAndUpdate(pos, state.setValue(colony.getAgeProperty(), 0));
            context.collect(new ItemStack(mushroom, age));
            return true;
        }
        List<ItemStack> drops = new ArrayList<>();
        CustomHarvestBehaviour.harvestBlock(context.level(), pos,
                net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), null,
                context.tool(new ItemStack(Items.SHEARS)), 1.0F, drops::add);
        if (context.replant())
            replantMushroom(context, pos, drops);
        drops.forEach(context::collect);
        return true;
    }

    private void harvestMushroom(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        int age = state.getValue(colony.getAgeProperty());
        if (age == 0 || age < colony.getMaxAge() && !CustomHarvestBehaviour.partial())
            return;
        context.world.playSound(null, pos, SoundEvents.MOOSHROOM_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
        context.world.setBlockAndUpdate(pos, state.setValue(colony.getAgeProperty(), 0));
        behaviour.dropItem(context, new ItemStack(mushroom, age));
    }

    private void harvestColony(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
        int age = state.getValue(colony.getAgeProperty());
        if (age < colony.getMaxAge() && !CustomHarvestBehaviour.partial())
            return;
        BlockHelper.destroyBlockAs(context.world, pos, null,
                CustomHarvestBehaviour.getHarvestTool(context, new ItemStack(Items.SHEARS)), 1.0F,
                stack -> behaviour.dropItem(context, stack));
        if (CustomHarvestBehaviour.replant()) {
            BlockState replanted = mushroom.defaultBlockState();
            if (replanted.canSurvive(context.world, pos)
                    && !ItemHelper.extract(context.contraption.getStorage().getAllItems(),
                            stack -> stack.is(mushroom.asItem()), 1, false).isEmpty())
                context.world.setBlockAndUpdate(pos, replanted);
        }
    }

    private void replantMushroom(AreaHarvestContext context, BlockPos pos, List<ItemStack> drops) {
        BlockState replanted = mushroom.defaultBlockState();
        if (!replanted.canSurvive(context.level(), pos))
            return;
        boolean hasSeed = false;
        for (ItemStack drop : drops) {
            if (drop.is(mushroom.asItem())) {
                drop.shrink(1);
                hasSeed = true;
                break;
            }
        }
        if (!hasSeed)
            hasSeed = !context.extractSeed(stack -> stack.is(mushroom.asItem()), 1).isEmpty();
        if (hasSeed)
            context.level().setBlockAndUpdate(pos, replanted);
    }
}
