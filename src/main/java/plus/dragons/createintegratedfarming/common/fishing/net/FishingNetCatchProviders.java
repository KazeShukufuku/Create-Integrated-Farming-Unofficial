/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.common.fishing.net;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class FishingNetCatchProviders {
    private static final Map<FishingNetMedium, LinkedHashMap<ResourceLocation, FishingNetCatchProvider>> PROVIDERS =
            new EnumMap<>(FishingNetMedium.class);

    static {
        for (FishingNetMedium medium : FishingNetMedium.values())
            PROVIDERS.put(medium, new LinkedHashMap<>());
    }

    private FishingNetCatchProviders() {}

    public static synchronized void register(
            ResourceLocation id, FishingNetMedium medium, FishingNetCatchProvider provider) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(medium, "medium");
        Objects.requireNonNull(provider, "provider");
        if (PROVIDERS.get(medium).putIfAbsent(id, provider) != null)
            throw new IllegalArgumentException("Duplicate fishing net catch provider: " + id + " for " + medium);
    }

    public static List<ItemStack> getCatch(FishingNetCatchContext context) {
        List<FishingNetCatchProvider> providers;
        synchronized (FishingNetCatchProviders.class) {
            providers = List.copyOf(PROVIDERS.get(context.medium()).values());
        }
        if (providers.isEmpty())
            return List.of();
        return providers.get(context.random().nextInt(providers.size())).getCatch(context);
    }
}
