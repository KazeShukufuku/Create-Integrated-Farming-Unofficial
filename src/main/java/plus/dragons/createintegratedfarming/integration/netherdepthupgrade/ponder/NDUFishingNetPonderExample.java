/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.netherdepthupgrade.ponder;

import com.scouter.netherdepthsupgrade.entity.NDUEntity;
import com.scouter.netherdepthsupgrade.items.NDUItems;
import plus.dragons.createintegratedfarming.client.ponder.FishingNetPonderExample;
import plus.dragons.createintegratedfarming.client.ponder.FishingNetPonderExamples;
import plus.dragons.createintegratedfarming.integration.ModIntegration;

public final class NDUFishingNetPonderExample {
    private NDUFishingNetPonderExample() {}

    public static void register() {
        FishingNetPonderExamples.register(new FishingNetPonderExample(
                ModIntegration.NETHER_DEPTHS_UPGRADE.asResource("obsidian_fish"),
                level -> NDUEntity.OBSIDIAN_FISH.get().create(level),
                () -> NDUItems.OBSIDIANFISH.get().getDefaultInstance()));
    }
}
