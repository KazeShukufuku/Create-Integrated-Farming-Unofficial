/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.common.fishing.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import plus.dragons.createintegratedfarming.config.CIFConfig;

public final class FishingNetEntityCaptures {
    private static final List<Predicate<LivingEntity>> PREDICATES = new ArrayList<>();

    static {
        PREDICATES.add(WaterAnimal.class::isInstance);
    }

    private FishingNetEntityCaptures() {}

    public static synchronized void register(Predicate<LivingEntity> predicate) {
        PREDICATES.add(Objects.requireNonNull(predicate, "predicate"));
    }

    public static boolean canCapture(LivingEntity entity) {
        if (entity instanceof Enemy)
            return false;
        boolean supported;
        synchronized (FishingNetEntityCaptures.class) {
            supported = PREDICATES.stream().anyMatch(predicate -> predicate.test(entity));
        }
        if (!supported)
            return false;
        var dimensions = entity.getDimensions(Pose.SWIMMING);
        float maxSize = CIFConfig.server().fishingNetCapturedCreatureMaxSize.getF();
        return dimensions.height <= maxSize && dimensions.width <= maxSize;
    }
}
