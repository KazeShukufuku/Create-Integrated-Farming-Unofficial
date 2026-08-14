/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.ranching;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import plus.dragons.createintegratedfarming.common.ranching.roost.AnimalRoostBlockEntity;

/** Shared Forge-1.20 implementation for optional bird-mod roosts. */
public class DynamicBirdRoostBlockEntity extends AnimalRoostBlockEntity {
    private final BirdRoostType bird;

    public DynamicBirdRoostBlockEntity(BirdRoostType bird, BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.bird = bird;
    }

    @Override
    protected ResourceLocation productionLootTable() {
        return bird.lootTable();
    }

    @Override
    protected SoundEvent productionSound() {
        return bird.eggSound();
    }

    @Override
    public boolean feedItem(ItemStack stack, boolean simulate) {
        assert level != null;
        if (feedCooldown > 0 || eggTime <= 0 || !stack.is(bird.foodTag()))
            return false;
        if (simulate)
            return true;
        eggTime = Math.max(0, eggTime - 2400);
        feedCooldown = 400 + level.random.nextInt(401);
        var facing = getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        Vec3 feedPos = Vec3.atBottomCenterOf(worldPosition)
                .add(facing.getStepX() * .5F, 13 / 16F, facing.getStepZ() * .5F);
        level.playSound(null, worldPosition, bird.ambientSound(), SoundSource.BLOCKS,
                1.0F, (level.random.nextFloat() - level.random.nextFloat()) * .2F + 1.0F);
        var remainder = stack.getCraftingRemainingItem();
        if (!remainder.isEmpty())
            Containers.dropItemStack(level, feedPos.x, feedPos.y, feedPos.z, remainder.copy());
        notifyUpdate();
        return true;
    }

}
