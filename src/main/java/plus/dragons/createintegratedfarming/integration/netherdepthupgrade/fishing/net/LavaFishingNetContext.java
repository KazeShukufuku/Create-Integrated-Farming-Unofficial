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

package plus.dragons.createintegratedfarming.integration.netherdepthupgrade.fishing.net;

import com.scouter.netherdepthsupgrade.entity.entities.LavaFishingBobberEntity;
import com.scouter.netherdepthsupgrade.loot.NDULootTables;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import plus.dragons.createintegratedfarming.common.fishing.net.AbstractFishingNetContext;

public class LavaFishingNetContext extends AbstractFishingNetContext<LavaFishingBobberEntity> {
    private static final Method CALCULATE_OPEN_LAVA = getCalculateOpenLavaMethod();

    public LavaFishingNetContext(ServerLevel level, ItemStack fishingRod) {
        super(level, fishingRod);
    }

    @Override
    protected LavaFishingBobberEntity createFishingHook(ServerLevel level) {
        return new LavaFishingBobberEntity(player, level, 0, 0);
    }

    @Override
    protected boolean isPosValidForFishing(ServerLevel level, BlockPos pos) {
        return level.getFluidState(pos).is(FluidTags.LAVA) &&
                level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
    }

    @Override
    public LootTable getLootTable(ServerLevel level, BlockPos pos) {
        if (level.dimension() == Level.NETHER)
            return level.getServer().getLootData().getLootTable(NDULootTables.NETHER_FISHING);
        return level.getServer().getLootData().getLootTable(NDULootTables.LAVA_FISHING);
    }

    public LootParams buildFishingLootContext(MovementContext context, ServerLevel level, BlockPos pos) {
        boolean openLava = calculateOpenLava(pos);
        fishingHook.openWater = openLava;
        return super.buildFishingLootContext(context, level, pos);
    }

    private boolean calculateOpenLava(BlockPos pos) {
        try {
            return (boolean) CALCULATE_OPEN_LAVA.invoke(fishingHook, pos);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Failed to calculate open lava for Lava Fishing Net", exception);
        }
    }

    private static Method getCalculateOpenLavaMethod() {
        try {
            var method = LavaFishingBobberEntity.class.getDeclaredMethod("calculateOpenLava", BlockPos.class);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException exception) {
            throw new IllegalStateException("Failed to find LavaFishingBobberEntity#calculateOpenLava", exception);
        }
    }
}
