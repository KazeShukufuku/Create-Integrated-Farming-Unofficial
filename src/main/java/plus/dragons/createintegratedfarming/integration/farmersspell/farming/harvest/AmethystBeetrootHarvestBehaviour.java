/* Copyright (C) 2026 DragonsPlus - SPDX-License-Identifier: LGPL-3.0-or-later */
package plus.dragons.createintegratedfarming.integration.farmersspell.farming.harvest;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public final class AmethystBeetrootHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    private final CropBlock crop;
    private final Item seed;

    private AmethystBeetrootHarvestBehaviour(CropBlock crop, Item seed) {
        this.crop = crop;
        this.seed = seed;
    }

    public static void register() {
        Block block = BuiltInRegistries.BLOCK.get(ModIntegration.FARMERS_SPELL.asResource("amethyst_beetroot"));
        Item seed = BuiltInRegistries.ITEM.get(ModIntegration.FARMERS_SPELL.asResource("amethyst_beetroot_seeds"));
        if (block instanceof CropBlock crop && block.defaultBlockState().hasProperty(BlockStateProperties.AGE_3)
                && seed != Items.AIR)
            CustomHarvestBehaviour.REGISTRY.register(block, new AmethystBeetrootHarvestBehaviour(crop, seed));
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!state.is(crop) || crop.getAge(state) == 0
                || !crop.isMaxAge(state) && !context.harvestPartiallyGrown())
            return false;
        // getStateForAge resets the attachment direction, breaking wall and ceiling crops.
        BlockState replanted = state.setValue(BlockStateProperties.AGE_3, 0);
        return HarvestOperations.harvestPlant(context, List.of(pos), pos, replanted, seed);
    }
}
