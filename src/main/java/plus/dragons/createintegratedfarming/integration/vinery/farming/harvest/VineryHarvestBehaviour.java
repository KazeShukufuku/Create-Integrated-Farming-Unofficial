/* Copyright (C) 2026 DragonsPlus - SPDX-License-Identifier: LGPL-3.0-or-later */
package plus.dragons.createintegratedfarming.integration.vinery.farming.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.satisfy.vinery.core.block.AppleLeavesBlock;
import net.satisfy.vinery.core.block.GrapeBush;
import net.satisfy.vinery.core.block.GrapeVineBlock;
import net.satisfy.vinery.core.block.LatticeBlock;
import net.satisfy.vinery.core.block.StemBlock;
import net.satisfy.vinery.core.block.entity.LatticeBlockEntity;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

/** Pick perennial fruit in place, including NetherVinery's GrapeBush subclasses. */
public final class VineryHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    public static void register() {
        var behaviour = new VineryHarvestBehaviour();
        CustomHarvestBehaviour.REGISTRY.registerProvider(block ->
                block instanceof GrapeBush || block instanceof GrapeVineBlock
                        || block instanceof StemBlock || block instanceof AppleLeavesBlock ? behaviour : null);
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof GrapeBush bush)
            return pickGrapes(context, pos, state, GrapeBush.AGE, bush.getType().getFruit());
        if (state.getBlock() instanceof GrapeVineBlock vine)
            return pickGrapes(context, pos, state, GrapeVineBlock.AGE, vine.type.getFruit());
        if (state.getBlock() instanceof StemBlock) {
            int age = state.getValue(StemBlock.AGE);
            boolean lattice = state.getBlock() instanceof LatticeBlock;
            Item fruit = state.getValue(StemBlock.GRAPE).getFruit();
            if (fruit == Items.AIR || age < 4 && !(context.harvestPartiallyGrown() && lattice && age == 3))
                return false;
            LatticeBlockEntity blockEntity = null;
            if (lattice) {
                if (!(context.level().getBlockEntity(pos) instanceof LatticeBlockEntity entity))
                    return false;
                blockEntity = entity;
            }
            int resetAge = lattice ? 1 : 2;
            int count = age == 4 ? 3 + context.level().random.nextInt(2) : 2;
            if (!HarvestOperations.pickFruit(context, pos, state.setValue(StemBlock.AGE, resetAge), new ItemStack(fruit, count)))
                return false;
            // Keep the original entity and its grape type / hanging-fruit appearance.
            if (blockEntity != null)
                blockEntity.setAge(resetAge);
            return true;
        }
        if (state.getBlock() instanceof AppleLeavesBlock
                && state.getValue(AppleLeavesBlock.HAS_APPLES) && state.getValue(AppleLeavesBlock.AGE) == 3) {
            int count = context.level().random.nextBoolean() ? 1 + context.level().random.nextInt(3) : 1;
            return HarvestOperations.pickFruit(context, pos,
                    state.setValue(AppleLeavesBlock.HAS_APPLES, false).setValue(AppleLeavesBlock.AGE, 1),
                    new ItemStack(Items.APPLE, count));
        }
        return false;
    }

    private static boolean pickGrapes(AreaHarvestContext context, BlockPos pos, BlockState state,
            IntegerProperty ageProperty, Item fruit) {
        int age = state.getValue(ageProperty);
        if (fruit == Items.AIR || age < 3 && !(context.harvestPartiallyGrown() && age == 2))
            return false;
        int count = context.level().random.nextInt(2) + (age == 3 ? 1 : 0);
        return HarvestOperations.pickFruit(context, pos, state.setValue(ageProperty, 1), new ItemStack(fruit, count));
    }
}
