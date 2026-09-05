/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.vanillabackport.registry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import plus.dragons.createintegratedfarming.common.ranching.roost.RoostCapturableProvider;
import plus.dragons.createintegratedfarming.integration.vanillabackport.ranching.roost.VanillaBackportChickenRoostBlock;

public final class VanillaBackportRoostCapturables {
    private VanillaBackportRoostCapturables() {}

    public static void register() {
        RoostCapturableProvider.REGISTRY.register(EntityType.CHICKEN, entity -> {
            if (!(entity instanceof Chicken chicken))
                return null;
            CompoundTag tag = new CompoundTag();
            chicken.saveWithoutId(tag);
            return switch (tag.getString(VanillaBackportChickenRoostBlock.VARIANT_TAG)) {
                case "minecraft:warm" -> VanillaBackportBlocks.CHICKEN_ROOST_WARM.get();
                case "minecraft:cold" -> VanillaBackportBlocks.CHICKEN_ROOST_COLD.get();
                default -> null;
            };
        });
    }
}
