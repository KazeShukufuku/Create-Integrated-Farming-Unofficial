/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.northstar.farming.harvest;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

/** Northstar's BushBlocks have separate block items and planting items. */
public final class NorthstarHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    private final IntegerProperty age;
    private final int maxAge;
    private final Item seed;

    private NorthstarHarvestBehaviour(IntegerProperty age, int maxAge, Item seed) {
        this.age = age;
        this.maxAge = maxAge;
        this.seed = seed;
    }

    public static void register() {
        for (String crop : List.of("mars_tulip", "mars_palm", "mars_sprout"))
            register(crop, crop + "_seeds", BlockStateProperties.AGE_2, 2);
        register("martian_strawberry_bush", "martian_strawberry", BlockStateProperties.AGE_5, 5);
    }

    private static void register(String crop, String seed, IntegerProperty age, int maxAge) {
        Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation("northstar", crop));
        Item item = BuiltInRegistries.ITEM.get(new ResourceLocation("northstar", seed));
        if (block.defaultBlockState().hasProperty(age))
            CustomHarvestBehaviour.REGISTRY.register(block, new NorthstarHarvestBehaviour(age, maxAge, item));
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        int growth = state.getValue(age);
        if (growth == 0 || growth < maxAge && !context.harvestPartiallyGrown())
            return false;
        // Use the original loot table, reserving one actual seed before publishing drops.
        // defaultBlockState is already mature for the three Martian flowers.
        return HarvestOperations.harvestPlant(context, List.of(pos), pos, state.setValue(age, 0), seed);
    }
}
