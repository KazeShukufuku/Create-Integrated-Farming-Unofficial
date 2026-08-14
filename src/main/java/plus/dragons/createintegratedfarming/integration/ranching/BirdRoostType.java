/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.ranching;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public enum BirdRoostType {
    ENVIRONMENTAL_DUCK("environmental", "duck", "entity.duck.ambient", "entity.duck.egg"),
    AUTUMNITY_TURKEY("autumnity", "turkey", "entity.turkey.ambient", "entity.turkey.egg");

    private final String namespace;
    private final String animal;
    private final String ambient;
    private final String egg;

    BirdRoostType(String namespace, String animal, String ambient, String egg) {
        this.namespace = namespace;
        this.animal = animal;
        this.ambient = ambient;
        this.egg = egg;
    }

    public EntityType<?> entityType() {
        return BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(namespace, animal));
    }

    public TagKey<Item> foodTag() {
        return TagKey.create(Registries.ITEM, new ResourceLocation(namespace, animal + "_food"));
    }

    public SoundEvent ambientSound() {
        return BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(namespace, ambient));
    }

    public SoundEvent eggSound() {
        return BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(namespace, egg));
    }

    public ResourceLocation lootTable() {
        return new ResourceLocation("create_integrated_farming", "gameplay/roost/" + namespace + "_" + animal);
    }
}
