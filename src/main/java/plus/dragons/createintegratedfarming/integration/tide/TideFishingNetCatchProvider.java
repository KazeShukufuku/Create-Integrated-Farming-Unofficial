/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.tide;

import com.li64.tide.Tide;
import com.li64.tide.compat.seasons.SeasonsCompat;
import com.li64.tide.data.fishing.FishingContext;
import com.li64.tide.data.fishing.mediums.FishingMedium;
import com.li64.tide.registries.TideEntityTypes;
import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import com.li64.tide.util.TideUtils;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchContext;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchProvider;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetMedium;
import plus.dragons.createintegratedfarming.mixin.tide.TideFishingHookAccessor;

public class TideFishingNetCatchProvider implements FishingNetCatchProvider {
    @Override
    public List<ItemStack> getCatch(FishingNetCatchContext context) {
        TideFishingHook hook = new TideFishingHook(TideEntityTypes.FISHING_BOBBER, context.level());
        hook.setOwner(context.player());
        hook.setPos(context.origin());
        ((TideFishingHookAccessor) hook).createintegratedfarming$setOpenWater(context.openFluid());
        // Tide 2.1.1 assumes an owned hook being removed is also the owner's active
        // Tide hook. This synthetic hook is never added to the level, so it must not
        // be discarded: doing so casts the fishing net's vanilla hook to HookAccessor.

        var exactBiome = context.level().getBiome(context.position());
        var nearestBiome = TideUtils.findClosestNonWaterBiome(context.level(), context.position(), 12, 2)
                .orElse(exactBiome);
        FishingMedium medium = context.medium() == FishingNetMedium.LAVA
                ? FishingMedium.LAVA
                : FishingMedium.WATER;
        FishingContext tideContext = new FishingContext(
                context.level(),
                hook,
                context.fishingRod(),
                context.random(),
                context.origin(),
                context.position(),
                Mth.floor(context.lootParams().getLuck()),
                medium.id().getPath(),
                exactBiome,
                nearestBiome,
                context.level().dimension(),
                Mth.clamp(TideUtils.getTemperatureAt(context.position(), context.level()), -1.0F, 1.0F),
                context.level().getMoonPhase(),
                SeasonsCompat.getSeason(context.level()));
        var result = Tide.FISHING_MANAGER.selectCatch(tideContext);
        if (!result.isEmpty())
            return result.items();
        return context.level().getServer().getLootData().getLootTable(BuiltInLootTables.FISHING_JUNK)
                .getRandomItems(tideContext.createFishingLootParams());
    }
}
