/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.tide;

import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchProviders;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetMedium;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public final class TideIntegration {
    private TideIntegration() {}

    public static void register() {
        var provider = new TideFishingNetCatchProvider();
        FishingNetCatchProviders.register(
                ModIntegration.TIDE.asResource("fishing_net"), FishingNetMedium.WATER, provider);
        FishingNetCatchProviders.register(
                ModIntegration.TIDE.asResource("fishing_net"), FishingNetMedium.LAVA, provider);
    }
}
