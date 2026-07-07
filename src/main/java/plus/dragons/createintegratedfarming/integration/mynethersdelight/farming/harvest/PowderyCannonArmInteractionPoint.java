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

package plus.dragons.createintegratedfarming.integration.mynethersdelight.farming.harvest;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.soytutta.mynethersdelight.common.block.PowderyCaneBlock;
import com.soytutta.mynethersdelight.common.block.PowderyCannonBlock;
import com.soytutta.mynethersdelight.common.block.PowderyFlowerBlock;
import com.soytutta.mynethersdelight.common.registry.MNDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PowderyCannonArmInteractionPoint extends ArmInteractionPoint {
    private static final int MAX_HARVEST_SCAN_HEIGHT = 7;

    public PowderyCannonArmInteractionPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
        super(type, level, pos, state);
    }

    @Override
    public Mode getMode() {
        return Mode.TAKE;
    }

    @Override
    public ItemStack extract(int slot, int amount, boolean simulate) {
        BlockPos harvestPos = findHarvestPos();
        if (harvestPos == null) {
            return ItemStack.EMPTY;
        }
        BlockState state = level.getBlockState(harvestPos);
        if (!simulate) {
            level.setBlockAndUpdate(harvestPos, resetHarvestedState(state));
        }
        int j = 1 + level.random.nextInt(2);
        return new ItemStack(MNDItems.BULLET_PEPPER.get(), j);
    }

    @Override
    public int getSlotCount() {
        return 1;
    }

    @Nullable
    private BlockPos findHarvestPos() {
        BlockPos currentPos = pos;
        for (int i = 0; i < MAX_HARVEST_SCAN_HEIGHT && currentPos.getY() < level.getMaxBuildHeight(); i++) {
            BlockState state = level.getBlockState(currentPos);
            if (isHarvestable(state)) {
                return currentPos;
            }
            if (!isPowderyCrop(state)) {
                return null;
            }
            currentPos = currentPos.above();
        }
        return null;
    }

    private static boolean isHarvestable(BlockState state) {
        return isPowderyCrop(state) && isLit(state);
    }

    private static boolean isPowderyCrop(BlockState state) {
        return state.getBlock() instanceof PowderyCaneBlock || state.getBlock() instanceof PowderyCannonBlock || state.getBlock() instanceof PowderyFlowerBlock;
    }

    private static BlockState resetHarvestedState(BlockState state) {
        BlockState result = setLit(state, false);
        result = setPressure(result, 0);
        if (state.getBlock() instanceof PowderyFlowerBlock) {
            if (result.hasProperty(PowderyFlowerBlock.AGE)) {
                result = result.setValue(PowderyFlowerBlock.AGE, 0);
            }
        }
        return result;
    }

    private static boolean isLit(BlockState state) {
        if (state.hasProperty(PowderyCaneBlock.LIT))
            return state.getValue(PowderyCaneBlock.LIT);
        if (state.hasProperty(PowderyCannonBlock.LIT))
            return state.getValue(PowderyCannonBlock.LIT);
        if (state.hasProperty(PowderyFlowerBlock.LIT))
            return state.getValue(PowderyFlowerBlock.LIT);
        return false;
    }

    private static BlockState setLit(BlockState state, boolean lit) {
        if (state.hasProperty(PowderyCaneBlock.LIT))
            return state.setValue(PowderyCaneBlock.LIT, lit);
        if (state.hasProperty(PowderyCannonBlock.LIT))
            return state.setValue(PowderyCannonBlock.LIT, lit);
        if (state.hasProperty(PowderyFlowerBlock.LIT))
            return state.setValue(PowderyFlowerBlock.LIT, lit);
        return state;
    }

    private static BlockState setPressure(BlockState state, int pressure) {
        if (state.hasProperty(PowderyCaneBlock.PRESSURE))
            return state.setValue(PowderyCaneBlock.PRESSURE, pressure);
        if (state.hasProperty(PowderyCannonBlock.PRESSURE))
            return state.setValue(PowderyCannonBlock.PRESSURE, pressure);
        if (state.hasProperty(PowderyFlowerBlock.PRESSURE))
            return state.setValue(PowderyFlowerBlock.PRESSURE, pressure);
        return state;
    }

    public static class Type extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return isPowderyCrop(state);
        }

        @Nullable
        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new PowderyCannonArmInteractionPoint(this, level, pos, state);
        }
    }
}
