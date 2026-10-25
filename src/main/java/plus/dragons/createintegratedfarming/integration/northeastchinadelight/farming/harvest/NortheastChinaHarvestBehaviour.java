/*
 * Copyright (C) 2026 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.northeastchinadelight.farming.harvest;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

/** Pick perennial crops in place, retaining attached faces and both corn segments. */
public final class NortheastChinaHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    public static void register() {
        var behaviour = new NortheastChinaHarvestBehaviour();
        CustomHarvestBehaviour.REGISTRY.registerProvider(block -> {
            var id = BuiltInRegistries.BLOCK.getKey(block);
            if (!id.getNamespace().equals(ModIntegration.NORTHEAST_CHINA_DELIGHT.id()))
                return null;
            return switch (id.getPath()) {
                case "wood_ear_crop", "hazelnut_bush", "corn_crop", "corn_stalk" -> behaviour;
                default -> null;
            };
        });
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!(context.level() instanceof ServerLevel level))
            return false;
        state = level.getBlockState(pos);
        var id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (!id.getNamespace().equals(ModIntegration.NORTHEAST_CHINA_DELIGHT.id()))
            return false;
        String path = id.getPath();
        if (path.equals("corn_crop") || path.equals("corn_stalk"))
            return harvestCorn(context, pos, path.equals("corn_stalk"));
        if (!path.equals("wood_ear_crop") && !path.equals("hazelnut_bush"))
            return false;
        if (!(state.getBlock().getStateDefinition().getProperty("age") instanceof IntegerProperty age))
            return false;
        int growth = state.getValue(age);
        boolean hazelnut = path.equals("hazelnut_bush");
        if (growth != 3 && !(hazelnut && context.harvestPartiallyGrown() && growth == 2))
            return false;
        List<ItemStack> drops = List.of();
        if (HarvestOperations.dropsEnabled(context)) {
            if (hazelnut) {
                // Hazelnut's break loot differs from its right-click harvest.
                drops = List.of(new ItemStack(BuiltInRegistries.ITEM.get(
                        ModIntegration.NORTHEAST_CHINA_DELIGHT.asResource("hazelnut")),
                        1 + level.random.nextInt(2) + (growth == 3 ? 1 : 0)));
            } else {
                drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos), null, context.tool());
            }
        }
        return HarvestOperations.pickFruit(context, pos, state.setValue(age, 1), drops.toArray(ItemStack[]::new));
    }

    private boolean harvestCorn(AreaHarvestContext context, BlockPos pos, boolean stalk) {
        BlockPos root = stalk ? pos.below() : pos;
        BlockPos top = root.above();
        var level = context.level();
        if (!HarvestOperations.canHarvest(context, List.of(root, top)))
            return false;
        BlockState lower = level.getBlockState(root);
        BlockState upper = level.getBlockState(top);
        if (!lower.is(BuiltInRegistries.BLOCK.get(ModIntegration.NORTHEAST_CHINA_DELIGHT.asResource("corn_crop")))
                || !upper.is(BuiltInRegistries.BLOCK.get(ModIntegration.NORTHEAST_CHINA_DELIGHT.asResource("corn_stalk")))
                || lower.getValue(BlockStateProperties.AGE_7) != 7
                || !(upper.getBlock().getStateDefinition().getProperty("stage") instanceof IntegerProperty stage)
                || upper.getValue(stage) != 2 || !context.claimHarvest(root))
            return false;
        // Only growth changes. Shape notifications between the two updates can
        // uproot the existing crop in low light underneath its own stalk.
        int flags = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;
        level.setBlock(root, lower.setValue(BlockStateProperties.AGE_7, 5), flags);
        level.setBlock(top, upper.setValue(stage, 0), flags);
        if (HarvestOperations.dropsEnabled(context))
            context.collect(new ItemStack(BuiltInRegistries.ITEM.get(
                    ModIntegration.NORTHEAST_CHINA_DELIGHT.asResource("corn")), 2));
        return true;
    }
}
