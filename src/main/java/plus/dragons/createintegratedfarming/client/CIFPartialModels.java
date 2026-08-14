/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.client;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import plus.dragons.createintegratedfarming.common.CIFCommon;

/** Partial models rendered in addition to their static block models. */
public final class CIFPartialModels {
    public static final PartialModel VACUUM_HARVESTER_MOVING =
            PartialModel.of(CIFCommon.asResource("block/vacuum_harvester/moving"));

    private CIFPartialModels() {}

    public static void init() {
        // Force class initialization during client setup.
    }
}
