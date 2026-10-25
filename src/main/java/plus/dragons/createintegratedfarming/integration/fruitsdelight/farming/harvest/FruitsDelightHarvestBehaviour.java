/*
 * Copyright (C) 2026 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.fruitsdelight.farming.harvest;

import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

/** Uses Fruits Delight's bundled L2 Harvester API, including its multi-block state updates. */
public final class FruitsDelightHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    private final Method getHarvestResult;
    private final Method updateState;
    private final Method drops;

    private FruitsDelightHarvestBehaviour(Class<?> harvestable, Class<?> result) throws ReflectiveOperationException {
        getHarvestResult = harvestable.getMethod("getHarvestResult", Level.class, BlockState.class, BlockPos.class);
        updateState = result.getMethod("updateState", Level.class, BlockPos.class);
        drops = result.getMethod("drops");
    }

    public static void register() {
        // Resolve the optional API once. Neither mod becomes a required binary dependency.
        try {
            Class<?> harvestable = Class.forName("dev.xkmc.l2harvester.api.HarvestableBlock");
            Class<?> result = Class.forName("dev.xkmc.l2harvester.api.HarvestResult");
            var behaviour = new FruitsDelightHarvestBehaviour(harvestable, result);
            CustomHarvestBehaviour.REGISTRY.registerProvider(block ->
                    BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(ModIntegration.FRUITS_DELIGHT.id())
                            && harvestable.isInstance(block) ? behaviour : null);
        } catch (ReflectiveOperationException exception) {
            CIFCommon.LOGGER.error("Failed to register Fruits Delight harvesting compatibility", exception);
        }
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!(context.level() instanceof ServerLevel level))
            return false;
        // A previous visit to the other half may already have harvested this plant.
        state = level.getBlockState(pos);
        var id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!id.getNamespace().equals(ModIntegration.FRUITS_DELIGHT.id())
                || !getHarvestResult.getDeclaringClass().isInstance(state.getBlock()))
            return false;
        if (id.getPath().equals("pineapple")) {
            int age = state.getValue(BlockStateProperties.AGE_4);
            if (age != 4 && !(context.harvestPartiallyGrown() && age > 0))
                return false;
            // Ripe pineapple drops only its fruit, so retain the plant instead of requiring another sapling.
            if (age == 4 && context.replant()) {
                if (!HarvestOperations.canHarvest(context, List.of(pos)) || !context.claimHarvest(pos))
                    return false;
                CustomHarvestBehaviour.harvestBlock(level, pos, state.setValue(BlockStateProperties.AGE_4, 0),
                        null, context.tool(), 1.0F, context::collect);
                return true;
            }
            return HarvestOperations.harvestPlant(context, List.of(pos), pos,
                    state.setValue(BlockStateProperties.AGE_4, 0),
                    BuiltInRegistries.ITEM.get(ModIntegration.FRUITS_DELIGHT.asResource("pineapple_sapling")));
        }

        BlockPos anchor = pos;
        List<BlockPos> parts = List.of(pos);
        var half = BlockStateProperties.DOUBLE_BLOCK_HALF;
        if (state.hasProperty(half)) {
            anchor = state.getValue(half) == DoubleBlockHalf.UPPER ? pos.below() : pos;
            parts = List.of(anchor, anchor.above());
            if (!HarvestOperations.canHarvest(context, parts))
                return false;
            BlockState lower = level.getBlockState(anchor);
            BlockState upper = level.getBlockState(anchor.above());
            if (!lower.is(state.getBlock()) || !upper.is(state.getBlock())
                    || lower.getValue(half) != DoubleBlockHalf.LOWER || upper.getValue(half) != DoubleBlockHalf.UPPER)
                return false;
        }
        if (!HarvestOperations.canHarvest(context, parts))
            return false;
        try {
            // The mod decides maturity, loot and how leaves/double bushes regrow.
            Object result = getHarvestResult.invoke(state.getBlock(), level, state, pos);
            if (result == null || !context.claimHarvest(anchor))
                return false;
            @SuppressWarnings("unchecked")
            List<ItemStack> harvested = (List<ItemStack>) drops.invoke(result);
            updateState.invoke(result, level, pos);
            if (HarvestOperations.dropsEnabled(context))
                harvested.forEach(context::collect);
            return true;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to harvest Fruits Delight crop at " + pos, exception);
        }
    }
}
