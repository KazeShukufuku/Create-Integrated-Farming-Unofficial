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

package plus.dragons.createintegratedfarming.client.ponder.scene;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint.Mode;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ArmPonderUtil {
    private ArmPonderUtil() {}

    public static ArmTarget armTarget(ResourceLocation type, BlockPos pos, Mode mode) {
        return new ArmTarget(type, pos, mode);
    }

    public static void configureArm(CreateSceneBuilder scene, SceneBuildingUtil util, BlockPos armPos, ArmTarget... targets) {
        scene.world().modifyBlockEntityNBT(util.select().position(armPos), ArmBlockEntity.class, nbt -> {
            ListTag points = new ListTag();
            for (ArmTarget target : targets) {
                points.add(target.serialize(armPos));
            }
            nbt.put("InteractionPoints", points);
            nbt.put("HeldItem", ItemStack.EMPTY.serializeNBT());
            NBTHelper.writeEnum(nbt, "Phase", ArmBlockEntity.Phase.SEARCH_INPUTS);
            nbt.putInt("TargetPointIndex", -1);
            nbt.putFloat("MovementProgress", 1);
        });
    }

    public record ArmTarget(ResourceLocation type, BlockPos pos, Mode mode) {
        private CompoundTag serialize(BlockPos armPos) {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("Type", type.toString());
            nbt.put("Pos", NbtUtils.writeBlockPos(pos.subtract(armPos)));
            NBTHelper.writeEnum(nbt, "Mode", mode);
            return nbt;
        }
    }
}
