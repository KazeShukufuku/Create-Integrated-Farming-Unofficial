/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.tide;

import com.li64.tide.registries.TideEntityTypes;
import com.li64.tide.registries.TideFish;
import net.minecraft.world.item.ItemStack;
import plus.dragons.createintegratedfarming.client.ponder.FishingNetPonderExample;
import plus.dragons.createintegratedfarming.client.ponder.FishingNetPonderExamples;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public final class TideFishingNetPonderExample {
    private TideFishingNetPonderExample() {}

    public static void register() {
        FishingNetPonderExamples.register(new FishingNetPonderExample(
                ModIntegration.TIDE.asResource("ash_perch"),
                level -> {
                    var entityType = TideEntityTypes.ENTITY_TYPES.get("ash_perch");
                    return entityType == null ? null : entityType.create(level);
                },
                () -> new ItemStack(TideFish.ASH_PERCH)));
    }
}
