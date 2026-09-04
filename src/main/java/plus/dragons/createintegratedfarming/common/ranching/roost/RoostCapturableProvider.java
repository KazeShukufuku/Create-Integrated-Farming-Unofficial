/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.common.ranching.roost;

import com.simibubi.create.api.registry.SimpleRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface RoostCapturableProvider {
    SimpleRegistry<EntityType<?>, RoostCapturableProvider> REGISTRY = SimpleRegistry.create();

    @Nullable
    RoostCapturable getCapturable(Entity entity);
}
