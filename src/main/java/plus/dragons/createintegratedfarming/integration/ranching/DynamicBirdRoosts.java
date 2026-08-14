/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.ranching;

import static plus.dragons.createintegratedfarming.common.CIFCommon.REGISTRATE;
import static plus.dragons.createintegratedfarming.common.registry.CIFBlocks.ROOST;

import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import plus.dragons.createintegratedfarming.common.ranching.roost.RoostCapturable;

public final class DynamicBirdRoosts {
    private DynamicBirdRoosts() {}

    public static void registerEnvironmental(IEventBus modBus) { Environmental.DUCK.getId(); }

    public static void registerAutumnity(IEventBus modBus) { Autumnity.TURKEY.getId(); }

    public static BlockEntityEntry<DynamicBirdRoostBlockEntity> blockEntity(BirdRoostType type) {
        return type == BirdRoostType.ENVIRONMENTAL_DUCK ? Environmental.ENTITY : Autumnity.ENTITY;
    }

    public static void registerEnvironmentalCapturable() {
        RoostCapturable.REGISTRY.register(BirdRoostType.ENVIRONMENTAL_DUCK.entityType(), Environmental.DUCK.get());
    }

    public static void registerAutumnityCapturable() {
        RoostCapturable.REGISTRY.register(BirdRoostType.AUTUMNITY_TURKEY.entityType(), Autumnity.TURKEY.get());
    }

    private static final class Environmental {
        private static final BlockEntry<DynamicBirdRoostBlock> DUCK = REGISTRATE
                .block("environmental_duck_roost", properties -> new DynamicBirdRoostBlock(properties, BirdRoostType.ENVIRONMENTAL_DUCK, ROOST))
                .lang("Duck Roost")
                .properties(properties -> properties.strength(1.5F).sound(SoundType.BAMBOO_WOOD))
                .blockstate((context, provider) -> provider.horizontalBlock(context.get(), AssetLookup.standardModel(context, provider)))
                .simpleItem().register();
        private static final BlockEntityEntry<DynamicBirdRoostBlockEntity> ENTITY = REGISTRATE
                .<DynamicBirdRoostBlockEntity>blockEntity("environmental_duck_roost", (type, pos, state) ->
                        new DynamicBirdRoostBlockEntity(BirdRoostType.ENVIRONMENTAL_DUCK, type, pos, state))
                .validBlock(DUCK).register();
    }

    private static final class Autumnity {
        private static final BlockEntry<DynamicBirdRoostBlock> TURKEY = REGISTRATE
                .block("autumnity_turkey_roost", properties -> new DynamicBirdRoostBlock(properties, BirdRoostType.AUTUMNITY_TURKEY, ROOST))
                .lang("Turkey Roost")
                .properties(properties -> properties.strength(1.5F).sound(SoundType.BAMBOO_WOOD))
                .blockstate((context, provider) -> provider.horizontalBlock(context.get(), AssetLookup.standardModel(context, provider)))
                .simpleItem().register();
        private static final BlockEntityEntry<DynamicBirdRoostBlockEntity> ENTITY = REGISTRATE
                .<DynamicBirdRoostBlockEntity>blockEntity("autumnity_turkey_roost", (type, pos, state) ->
                        new DynamicBirdRoostBlockEntity(BirdRoostType.AUTUMNITY_TURKEY, type, pos, state))
                .validBlock(TURKEY).register();
    }

    public static Block environmentalDuck() {
        return Environmental.DUCK.get();
    }

    public static Block autumnityTurkey() {
        return Autumnity.TURKEY.get();
    }
}
