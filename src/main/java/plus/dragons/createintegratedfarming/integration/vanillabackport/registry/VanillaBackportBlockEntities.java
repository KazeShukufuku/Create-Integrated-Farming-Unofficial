/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.vanillabackport.registry;

import static plus.dragons.createintegratedfarming.common.CIFCommon.REGISTRATE;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import plus.dragons.createintegratedfarming.integration.vanillabackport.ranching.roost.VanillaBackportChickenRoostBlockEntity;

public final class VanillaBackportBlockEntities {
    public static final BlockEntityEntry<VanillaBackportChickenRoostBlockEntity> CHICKEN_ROOST = REGISTRATE
            .blockEntity("vanillabackport_chicken_roost", VanillaBackportChickenRoostBlockEntity::new)
            .validBlocks(VanillaBackportBlocks.CHICKEN_ROOST_WARM, VanillaBackportBlocks.CHICKEN_ROOST_COLD)
            .register();

    private VanillaBackportBlockEntities() {}

    public static void register() {}
}
