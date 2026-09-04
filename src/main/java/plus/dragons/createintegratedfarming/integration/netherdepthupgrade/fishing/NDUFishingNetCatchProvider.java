/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.netherdepthupgrade.fishing;

import com.scouter.netherdepthsupgrade.loot.NDULootTables;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchContext;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchProvider;

public class NDUFishingNetCatchProvider implements FishingNetCatchProvider {
    @Override
    public List<ItemStack> getCatch(FishingNetCatchContext context) {
        var lootTable = context.level().dimension() == Level.NETHER
                ? NDULootTables.NETHER_FISHING
                : NDULootTables.LAVA_FISHING;
        return context.level().getServer().getLootData().getLootTable(lootTable)
                .getRandomItems(context.lootParams());
    }
}
