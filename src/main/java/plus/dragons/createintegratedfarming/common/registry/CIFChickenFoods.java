/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.common.registry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.AddReloadListenerEvent;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.ranching.roost.chicken.ChickenFoodFluid;
import plus.dragons.createintegratedfarming.common.ranching.roost.chicken.ChickenFoodItem;

public class CIFChickenFoods {
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DIRECTORY = "chicken_food";
    private static final String ITEMS = "items/";
    private static final String FLUIDS = "fluids/";
    private static List<ItemFoodEntry> itemFoods = List.of();
    private static List<FluidFoodEntry> fluidFoods = List.of();

    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new ReloadListener());
    }

    public static @Nullable ChickenFoodItem getItemFood(ItemStack stack) {
        for (var entry : itemFoods) {
            if (entry.ingredient.test(stack))
                return entry.food;
        }
        return null;
    }

    public static @Nullable ChickenFoodFluid getFluidFood(Fluid fluid) {
        for (var entry : fluidFoods) {
            if (entry.matches(fluid))
                return entry.food;
        }
        return null;
    }

    private static ItemFoodEntry parseItemFood(ResourceLocation id, JsonObject json) {
        var ingredient = Ingredient.fromJson(GsonHelper.getNonNull(json, "ingredient"));
        var food = new ChickenFoodItem(
                getProgress(id, json),
                getCooldown(id, json),
                parseRemainingItem(id, json));
        return new ItemFoodEntry(ingredient, food);
    }

    private static FluidFoodEntry parseFluidFood(ResourceLocation id, JsonObject json) {
        var fluidId = Optional.<ResourceLocation>empty();
        var fluidTag = Optional.<TagKey<Fluid>>empty();
        var required = GsonHelper.getAsBoolean(json, "required", true);
        if (json.has("fluid")) {
            var resource = new ResourceLocation(GsonHelper.getAsString(json, "fluid"));
            if (BuiltInRegistries.FLUID.getOptional(resource).isPresent()) {
                fluidId = Optional.of(resource);
            } else if (required) {
                CIFCommon.LOGGER.warn("Skipping unknown chicken food fluid {} in {}", resource, id);
            }
        }
        if (json.has("tag")) {
            fluidTag = Optional.of(TagKey.create(Registries.FLUID, new ResourceLocation(GsonHelper.getAsString(json, "tag"))));
        }
        if (required && fluidId.isEmpty() && fluidTag.isEmpty()) {
            throw new IllegalArgumentException("Expected either 'fluid' or 'tag'");
        }
        var food = new ChickenFoodFluid(
                getProgress(id, json),
                getCooldown(id, json),
                GsonHelper.getAsInt(json, "amount"));
        return new FluidFoodEntry(fluidId, fluidTag, food);
    }

    private static IntProvider getProgress(ResourceLocation id, JsonObject json) {
        return parseIntProvider(id, GsonHelper.getNonNull(json, "progress"), "progress", 0, 12000);
    }

    private static IntProvider getCooldown(ResourceLocation id, JsonObject json) {
        return parseIntProvider(id, GsonHelper.getNonNull(json, "cooldown"), "cooldown", 0, Integer.MAX_VALUE);
    }

    private static IntProvider parseIntProvider(ResourceLocation id, JsonElement json, String field, int min, int max) {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            int value = json.getAsInt();
            if (value < min || value > max) {
                throw new IllegalArgumentException("Field '" + field + "' in " + id + " is outside allowed range");
            }
            return ConstantInt.of(value);
        }
        if (json.isJsonObject()) {
            var object = json.getAsJsonObject();
            if (object.has("value")) {
                int value = GsonHelper.getAsInt(object, "value");
                if (value < min || value > max) {
                    throw new IllegalArgumentException("Field '" + field + "' in " + id + " is outside allowed range");
                }
                return ConstantInt.of(value);
            }
            if (object.has("min_inclusive") && object.has("max_inclusive")) {
                int minInclusive = GsonHelper.getAsInt(object, "min_inclusive");
                int maxInclusive = GsonHelper.getAsInt(object, "max_inclusive");
                if (minInclusive < min || maxInclusive > max || minInclusive > maxInclusive) {
                    throw new IllegalArgumentException("Field '" + field + "' in " + id + " has invalid uniform bounds");
                }
                return UniformInt.of(minInclusive, maxInclusive);
            }
        }
        var codec = field.equals("progress") ? IntProvider.codec(min, max) : IntProvider.NON_NEGATIVE_CODEC;
        return codec.parse(JsonOps.INSTANCE, json)
                .getOrThrow(false, error -> CIFCommon.LOGGER.error("Failed to parse chicken food {} in {}: {}", field, id, error));
    }

    private static Optional<ItemStack> parseRemainingItem(ResourceLocation id, JsonObject json) {
        if (!json.has("using_converts_to"))
            return Optional.empty();
        var remaining = GsonHelper.getAsJsonObject(json, "using_converts_to");
        var itemId = new ResourceLocation(GsonHelper.getAsString(remaining, "item"));
        var item = BuiltInRegistries.ITEM.getOptional(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown remaining item '" + itemId + "' in " + id));
        int count = GsonHelper.getAsInt(remaining, "count", 1);
        if (count < 1)
            throw new IllegalArgumentException("Remaining item count must be positive in " + id);
        return Optional.of(new ItemStack(item, count));
    }

    private record ItemFoodEntry(Ingredient ingredient, ChickenFoodItem food) {
    }

    private record FluidFoodEntry(Optional<ResourceLocation> fluidId, Optional<TagKey<Fluid>> fluidTag, ChickenFoodFluid food) {
        boolean matches(Fluid fluid) {
            if (fluid == Fluids.EMPTY)
                return false;
            if (fluidId.isPresent() && fluidId.get().equals(BuiltInRegistries.FLUID.getKey(fluid)))
                return true;
            return fluidTag.isPresent() && fluid.is(fluidTag.get());
        }
    }

    private static class ReloadListener extends SimpleJsonResourceReloadListener {
        private ReloadListener() {
            super(GSON, DIRECTORY);
        }

        @Override
        protected void apply(
                java.util.Map<ResourceLocation, JsonElement> resources,
                ResourceManager manager,
                ProfilerFiller profiler) {
            var loadedItemFoods = new ArrayList<ItemFoodEntry>();
            var loadedFluidFoods = new ArrayList<FluidFoodEntry>();
            resources.forEach((id, json) -> {
                try {
                    var object = GsonHelper.convertToJsonObject(json, id.toString());
                    var path = id.getPath();
                    if (path.startsWith(ITEMS)) {
                        loadedItemFoods.add(parseItemFood(id, object));
                    } else if (path.startsWith(FLUIDS)) {
                        loadedFluidFoods.add(parseFluidFood(id, object));
                    } else {
                        CIFCommon.LOGGER.warn("Ignoring chicken food entry outside items/ or fluids/: {}", id);
                    }
                } catch (RuntimeException exception) {
                    CIFCommon.LOGGER.error("Failed to load chicken food entry {}", id, exception);
                }
            });
            itemFoods = List.copyOf(loadedItemFoods);
            fluidFoods = List.copyOf(loadedFluidFoods);
            CIFCommon.LOGGER.info("Loaded {} item chicken food entries and {} fluid chicken food entries",
                    itemFoods.size(), fluidFoods.size());
        }
    }
}
