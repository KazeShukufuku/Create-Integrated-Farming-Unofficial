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

package plus.dragons.createintegratedfarming.integration.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.ranching.roost.AnimalRoostBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public enum RoostNextEggProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    public static final ResourceLocation UID = CIFCommon.asResource("roost_next_egg");
    private static final String NEXT_EGG_IN = "NextEggIn";
    private static final String FEED_COOLDOWN = "FeedCooldown";

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag serverData = accessor.getServerData();
        if (!serverData.contains(NEXT_EGG_IN))
            return;
        tooltip.add(Component.translatable(
                "jade.nextEgg",
                IThemeHelper.get().seconds(serverData.getInt(NEXT_EGG_IN))));
        if (serverData.contains(FEED_COOLDOWN)) {
            tooltip.add(Component.translatable(
                    "create_integrated_farming.jade.feed_cooldown",
                    IThemeHelper.get().seconds(serverData.getInt(FEED_COOLDOWN))));
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        BlockEntity blockEntity = accessor.getBlockEntity();
        if (blockEntity instanceof AnimalRoostBlockEntity roost) {
            tag.putInt(NEXT_EGG_IN, roost.getEggTime());
            if (roost.getFeedCooldown() > 0)
                tag.putInt(FEED_COOLDOWN, roost.getFeedCooldown());
        }
    }

    @Override
    public @NotNull ResourceLocation getUid() {
        return UID;
    }
}
