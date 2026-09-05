/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.vanillabackport.ranching.roost;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.common.ranching.roost.chicken.ChickenRoostBlockEntity;
import plus.dragons.createintegratedfarming.common.registry.CIFLootTables;
import plus.dragons.createintegratedfarming.integration.vanillabackport.registry.VanillaBackportBlocks;
import plus.dragons.createintegratedfarming.integration.vanillabackport.registry.VanillaBackportLootTables;

public class VanillaBackportChickenRoostBlockEntity extends ChickenRoostBlockEntity {
    public VanillaBackportChickenRoostBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected ResourceLocation productionLootTable() {
        if (getBlockState().is(VanillaBackportBlocks.CHICKEN_ROOST_WARM.get()))
            return VanillaBackportLootTables.WARM_CHICKEN_ROOST;
        if (getBlockState().is(VanillaBackportBlocks.CHICKEN_ROOST_COLD.get()))
            return VanillaBackportLootTables.COLD_CHICKEN_ROOST;
        return CIFLootTables.CHICKEN_ROOST;
    }
}
