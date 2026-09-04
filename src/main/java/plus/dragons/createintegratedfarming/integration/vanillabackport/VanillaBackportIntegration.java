/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.vanillabackport;

import net.minecraftforge.eventbus.api.IEventBus;
import plus.dragons.createintegratedfarming.integration.vanillabackport.registry.VanillaBackportBlockEntities;
import plus.dragons.createintegratedfarming.integration.vanillabackport.registry.VanillaBackportBlocks;

public final class VanillaBackportIntegration {
    private VanillaBackportIntegration() {}

    public static void register(IEventBus modBus) {
        VanillaBackportBlocks.register();
        VanillaBackportBlockEntities.register();
    }
}
