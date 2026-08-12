/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.ranching;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import plus.dragons.createintegratedfarming.common.ranching.roost.RoostBlock;
import plus.dragons.createintegratedfarming.common.ranching.roost.RoostCapturable;
import plus.dragons.createintegratedfarming.integration.ranching.DynamicBirdRoosts;

public class DynamicBirdRoostBlock extends RoostBlock implements IBE<DynamicBirdRoostBlockEntity>, RoostCapturable {
    private final BirdRoostType bird;
    private final Supplier<? extends Block> empty;

    public DynamicBirdRoostBlock(Properties properties, BirdRoostType bird, Supplier<? extends Block> empty) {
        super(properties);
        this.bird = bird;
        this.empty = empty;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) {
            return onBlockEntityUse(level, pos, roost -> {
                ItemStack extracted = roost.outputHandler.extractItem(0, 64, false);
                if (extracted.isEmpty())
                    return InteractionResult.PASS;
                player.getInventory().placeItemBackInInventory(extracted);
                level.playSound(player, pos, bird.eggSound(), SoundSource.BLOCKS, 1.0F,
                        (level.random.nextFloat() - level.random.nextFloat()) * .2F + 1.0F);
                return InteractionResult.sidedSuccess(level.isClientSide);
            });
        }
        if (stack.is(Items.LEAD)) {
            Entity released = bird.entityType().create(level);
            if (released == null)
                return InteractionResult.PASS;
            released.setPos(pos.getCenter());
            if (released instanceof Mob mob)
                mob.setLeashedTo(player, true);
            level.addFreshEntity(released);
            level.setBlockAndUpdate(pos, empty.get().withPropertiesOf(state));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return onBlockEntityUse(level, pos, roost -> {
            if (!roost.feedItem(stack, false))
                return InteractionResult.PASS;
            if (!player.getAbilities().instabuild)
                stack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        });
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return ItemHelper.calcRedstoneFromBlockEntity(this, level, pos);
    }

    @Override
    public Class<DynamicBirdRoostBlockEntity> getBlockEntityClass() {
        return DynamicBirdRoostBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends DynamicBirdRoostBlockEntity> getBlockEntityType() {
        return DynamicBirdRoosts.blockEntity(bird).get();
    }

    @Override
    public InteractionResult captureBlock(Level level, BlockState state, BlockPos pos, ItemStack stack, Player player, Entity entity) {
        if (entity.getType() != bird.entityType() || entity instanceof AgeableMob animal && animal.isBaby())
            return InteractionResult.PASS;
        level.setBlockAndUpdate(pos, withPropertiesOf(state));
        entity.playSound(bird.ambientSound());
        entity.discard();
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResult captureItem(Level level, ItemStack stack, InteractionHand hand, Player player, Entity entity) {
        if (entity.getType() != bird.entityType() || entity instanceof AgeableMob animal && animal.isBaby())
            return InteractionResult.PASS;
        ItemStack roost = new ItemStack(this);
        if (player.getAbilities().instabuild)
            player.getInventory().placeItemBackInInventory(roost);
        else if (stack.getCount() == 1)
            player.setItemInHand(hand, roost);
        else {
            player.getInventory().placeItemBackInInventory(roost);
            stack.shrink(1);
        }
        entity.playSound(bird.ambientSound());
        entity.discard();
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
