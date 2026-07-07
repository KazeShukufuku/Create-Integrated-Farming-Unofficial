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

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;

public class FishingNetContext extends AbstractFishingNetContext<FishingHook> {
    public FishingNetContext(ServerLevel level, ItemStack fishingRod) {
        super(level, fishingRod);
    }

    @Override
    protected FishingHook createFishingHook(ServerLevel level) {
        return new FishingHook(EntityType.FISHING_BOBBER, level);
    }

    @Override
    protected boolean isPosValidForFishing(ServerLevel level, BlockPos pos) {
        return level.getFluidState(pos).is(FluidTags.WATER);
    }

    @Override
    public LootTable getLootTable(ServerLevel level, BlockPos pos) {
        return level.getServer().getLootData().getLootTable(BuiltInLootTables.FISHING);
    }

    public LootParams buildFishingLootContext(MovementContext context, ServerLevel level, BlockPos pos) {
        return super.buildFishingLootContext(context, level, pos);
    }
}
