/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.farmersdelight.registry;

import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.farmersdelight.farming.harvest.MushroomColonyHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.farmersdelight.farming.harvest.RiceHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.farmersdelight.farming.harvest.TomatoHarvestBehaviour;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public final class FDHarvestBehaviours {
    private FDHarvestBehaviours() {}

    public static void register() {
        var rice = new RiceHarvestBehaviour();
        CustomHarvestBehaviour.REGISTRY.register(ModBlocks.RICE_CROP.get(), rice);
        CustomHarvestBehaviour.REGISTRY.register(ModBlocks.RICE_CROP_PANICLES.get(), rice);
        CustomHarvestBehaviour.REGISTRY.registerProvider(MushroomColonyHarvestBehaviour::create);
        CustomHarvestBehaviour.REGISTRY.registerProvider(TomatoHarvestBehaviour::create);
    }
}
