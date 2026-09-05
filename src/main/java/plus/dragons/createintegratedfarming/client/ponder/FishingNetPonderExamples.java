/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.client.ponder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public final class FishingNetPonderExamples {
    private static final Map<ResourceLocation, FishingNetPonderExample> EXAMPLES = new LinkedHashMap<>();

    private FishingNetPonderExamples() {}

    public static synchronized void register(FishingNetPonderExample example) {
        if (EXAMPLES.putIfAbsent(example.id(), example) != null)
            throw new IllegalArgumentException("Duplicate fishing net Ponder example: " + example.id());
    }

    public static synchronized List<FishingNetPonderExample> shuffled() {
        List<FishingNetPonderExample> examples = new ArrayList<>(EXAMPLES.values());
        Collections.shuffle(examples);
        return examples;
    }
}
