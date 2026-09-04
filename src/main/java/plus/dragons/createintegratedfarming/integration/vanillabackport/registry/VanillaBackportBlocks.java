/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.vanillabackport.registry;

import static plus.dragons.createintegratedfarming.common.CIFCommon.REGISTRATE;
import static plus.dragons.createintegratedfarming.common.registry.CIFBlocks.ROOST;

import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import plus.dragons.createintegratedfarming.integration.vanillabackport.ranching.roost.VanillaBackportChickenRoostBlock;

public final class VanillaBackportBlocks {
    public static final BlockEntry<VanillaBackportChickenRoostBlock> CHICKEN_ROOST_WARM = registerChickenRoost(
            "vanillabackport_chicken_roost_warm", "Warm Chicken Roost",
            VanillaBackportChickenRoostBlock.WARM_VARIANT);
    public static final BlockEntry<VanillaBackportChickenRoostBlock> CHICKEN_ROOST_COLD = registerChickenRoost(
            "vanillabackport_chicken_roost_cold", "Cold Chicken Roost",
            VanillaBackportChickenRoostBlock.COLD_VARIANT);

    private VanillaBackportBlocks() {}

    private static BlockEntry<VanillaBackportChickenRoostBlock> registerChickenRoost(
            String path, String name, ResourceLocation variant) {
        return REGISTRATE.block(path,
                        properties -> new VanillaBackportChickenRoostBlock(properties, ROOST, variant))
                .lang(name)
                .properties(properties -> properties
                        .strength(1.5F)
                        .sound(SoundType.BAMBOO_WOOD)
                        .noLootTable())
                .blockstate((context, provider) -> provider.horizontalBlock(
                        context.get(), AssetLookup.standardModel(context, provider)))
                .item()
                .build()
                .register();
    }

    public static void register() {}
}
