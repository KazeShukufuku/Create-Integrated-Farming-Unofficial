/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package plus.dragons.createintegratedfarming.integration.netherexp.farming.harvest;

import net.jadenxgamer.netherexp.registry.block.JNEBlocks;
import net.jadenxgamer.netherexp.registry.block.custom.CerebrageBlock;
import net.jadenxgamer.netherexp.registry.item.JNEItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public class CerebrageHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    public static void register() {
        CustomHarvestBehaviour.REGISTRY.register(JNEBlocks.CEREBRAGE_SKULL.get(), new CerebrageHarvestBehaviour());
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        int age = state.getValue(CerebrageBlock.AGE);
        if (age < 3 || (age == 4 && (!context.level().isLoaded(pos.above()) || !context.level().isEmptyBlock(pos.above()))))
            return false;
        // Jaden's Nether Expansion 1.20.1 has no config for these; mirror its
        // CerebrageBlock#use drops: 3-5 cerebrage plus up to 1 seed at age 3,
        // and up to 1 seed at age 4 (when the item can pop out above).
        ItemStack cerebrage = age == 3
                ? new ItemStack(JNEItems.CEREBRAGE.get(), context.level().random.nextInt(3, 6))
                : ItemStack.EMPTY;
        ItemStack seeds = new ItemStack(JNEItems.CEREBRAGE_SEEDS.get(), context.level().random.nextInt(-5, 2));
        return HarvestOperations.pickFruit(context, pos, state.setValue(CerebrageBlock.AGE, 1), cerebrage, seeds);
    }
}
