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

package plus.dragons.createintegratedfarming.common.fishing.net;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import plus.dragons.createintegratedfarming.config.CIFConfig;

public class FishingNetContext extends AbstractFishingNetContext<FishingHook> {
    public FishingNetContext(ServerLevel level, ItemStack fishingRod) {
        super(level, fishingRod);
    }

    @Override
    protected FishingHook createFishingHook(ServerLevel level) {
        return new FishingHook(EntityType.FISHING_BOBBER, level);
    }

    @Override
    public boolean isPosValidForFishing(ServerLevel level, BlockPos pos) {
        return level.getFluidState(pos).is(FluidTags.WATER)
                && level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
    }

    @Override
    protected FishingNetMedium getMedium() {
        return FishingNetMedium.WATER;
    }

    @Override
    protected boolean isOpenFluid(ServerLevel level, BlockPos pos) {
        if (!CIFConfig.server().fishingNetChecksOpenWater.get())
            return false;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        boolean foundAir = false;
        for (int y = -1; y <= 2; y++) {
            boolean allSourceWater = true;
            boolean allAir = true;
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    cursor.setWithOffset(pos, x, y, z);
                    var state = level.getBlockState(cursor);
                    var fluid = state.getFluidState();
                    allSourceWater &= fluid.is(FluidTags.WATER)
                            && fluid.isSource()
                            && state.getCollisionShape(level, cursor).isEmpty();
                    allAir &= state.isAir();
                }
            }
            if (!allSourceWater && !allAir)
                return false;
            if (allAir) {
                if (y == -1)
                    return false;
                foundAir = true;
            } else if (foundAir) {
                return false;
            }
        }
        return true;
    }
}
