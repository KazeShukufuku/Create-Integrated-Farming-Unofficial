/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.client.ponder;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record FishingNetPonderExample(
        ResourceLocation id, Function<Level, Entity> entityFactory, Supplier<ItemStack> iconSupplier) {
    public FishingNetPonderExample {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(entityFactory, "entityFactory");
        Objects.requireNonNull(iconSupplier, "iconSupplier");
    }
}
