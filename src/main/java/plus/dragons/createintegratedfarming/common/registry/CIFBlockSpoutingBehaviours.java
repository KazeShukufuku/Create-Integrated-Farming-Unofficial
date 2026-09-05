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

package plus.dragons.createintegratedfarming.common.registry;

import com.simibubi.create.api.behaviour.spouting.BlockSpoutingBehaviour;
import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import plus.dragons.createintegratedfarming.common.ranching.roost.AnimalRoostBlockEntity;
import plus.dragons.createintegratedfarming.common.ranching.roost.RoostBlock;

public class CIFBlockSpoutingBehaviours {
    private static final BlockSpoutingBehaviour ROOST_FEEDING = CIFBlockSpoutingBehaviours::fillRoost;

    public static void register() {
        BlockSpoutingBehaviour.BY_BLOCK.registerProvider(
                block -> block instanceof RoostBlock ? ROOST_FEEDING : null);
    }

    private static int fillRoost(Level level, BlockPos pos, SpoutBlockEntity spout, FluidStack fluid, boolean simulate) {
        if (level.getBlockEntity(pos) instanceof AnimalRoostBlockEntity roost)
            return roost.feedFluid(fluid, simulate);
        return 0;
    }
}
