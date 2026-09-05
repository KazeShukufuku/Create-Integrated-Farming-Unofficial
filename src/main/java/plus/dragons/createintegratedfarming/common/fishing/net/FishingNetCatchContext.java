/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.common.fishing.net;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;

public record FishingNetCatchContext(
        ServerLevel level,
        BlockPos position,
        Vec3 origin,
        FishingNetMedium medium,
        ServerPlayer player,
        FishingHook fishingHook,
        ItemStack fishingRod,
        LootParams lootParams,
        boolean openFluid,
        RandomSource random) {}
