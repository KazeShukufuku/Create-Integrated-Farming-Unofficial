/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.ranching;

import net.minecraft.core.registries.BuiltInRegistries;
import plus.dragons.createintegratedfarming.client.ponder.CIFPonderPlugin;

/** Ponder registration shared by the optional Environmental and Autumnity roosts. */
public final class DynamicBirdRoostPonderPlugin {
    private DynamicBirdRoostPonderPlugin() {}

    public static void registerEnvironmental() {
        register(DynamicBirdRoosts::environmentalDuck);
    }

    public static void registerAutumnity() {
        register(DynamicBirdRoosts::autumnityTurkey);
    }

    private static void register(java.util.function.Supplier<net.minecraft.world.level.block.Block> roost) {
        CIFPonderPlugin.registerRoosts(BuiltInRegistries.BLOCK.getKey(roost.get()));
    }
}
