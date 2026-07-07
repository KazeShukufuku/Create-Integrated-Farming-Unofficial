/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createdragonsplus.data.recipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class ShapedRecipeBuilder implements Consumer<Consumer<FinishedRecipe>> {
    private final @Nullable String directory;
    private final List<ICondition> conditions = new ArrayList<>();
    private final Map<Character, Ingredient> key = new LinkedHashMap<>();
    private final List<String> pattern = new ArrayList<>();
    private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();
    private @Nullable ResourceLocation id;
    private ItemStack result = ItemStack.EMPTY;
    private RecipeCategory category = RecipeCategory.MISC;
    private String group = "";

    public ShapedRecipeBuilder(@Nullable String directory) {
        this.directory = directory;
    }

    public ShapedRecipeBuilder define(Character symbol, TagKey<Item> tag) {
        key.put(symbol, Ingredient.of(tag));
        return this;
    }

    public ShapedRecipeBuilder define(Character symbol, ItemLike item) {
        key.put(symbol, Ingredient.of(item));
        return this;
    }

    public ShapedRecipeBuilder define(Character symbol, Ingredient ingredient) {
        key.put(symbol, ingredient);
        return this;
    }

    public ShapedRecipeBuilder pattern(String line) {
        pattern.add(line);
        return this;
    }

    public ShapedRecipeBuilder output(ItemLike item) {
        this.result = new ItemStack(item);
        return this;
    }

    public ShapedRecipeBuilder output(ItemLike item, int count) {
        this.result = new ItemStack(item, count);
        return this;
    }

    public ShapedRecipeBuilder output(ItemStack stack) {
        this.result = stack;
        return this;
    }

    public ShapedRecipeBuilder unlockedBy(String name, CriterionTriggerInstance criterion) {
        criteria.put(name, criterion);
        return this;
    }

    public ShapedRecipeBuilder category(RecipeCategory category) {
        this.category = category;
        return this;
    }

    public ShapedRecipeBuilder group(String group) {
        this.group = group;
        return this;
    }

    public ShapedRecipeBuilder withId(ResourceLocation id) {
        this.id = id;
        return this;
    }

    public ShapedRecipeBuilder withCondition(ICondition condition) {
        conditions.add(condition);
        return this;
    }

    @Override
    public void accept(Consumer<FinishedRecipe> output) {
        ResourceLocation recipeId = prefixedId();
        net.minecraft.data.recipes.ShapedRecipeBuilder builder = createVanillaBuilder();
        if (conditions.isEmpty()) {
            builder.save(output, recipeId);
            return;
        }
        builder.save(recipe -> output.accept(withConditions(recipe)), recipeId);
    }

    private ResourceLocation prefixedId() {
        ResourceLocation baseId = id != null ? id : getDefaultId();
        if (directory == null || baseId.getPath().startsWith(directory + "/")) {
            return baseId;
        }
        return new ResourceLocation(baseId.getNamespace(), directory + "/" + baseId.getPath());
    }

    private ResourceLocation getDefaultId() {
        if (result.isEmpty()) {
            throw new IllegalStateException("Recipe output must be set before saving");
        }
        return BuiltInRegistries.ITEM.getKey(result.getItem());
    }

    private net.minecraft.data.recipes.ShapedRecipeBuilder createVanillaBuilder() {
        if (result.isEmpty()) {
            throw new IllegalStateException("Recipe output must be set before saving");
        }
        net.minecraft.data.recipes.ShapedRecipeBuilder builder = net.minecraft.data.recipes.ShapedRecipeBuilder
                .shaped(category, result.getItem(), result.getCount());
        key.forEach(builder::define);
        pattern.forEach(builder::pattern);
        criteria.forEach(builder::unlockedBy);
        if (!group.isEmpty()) {
            builder.group(group);
        }
        return builder;
    }

    private FinishedRecipe withConditions(FinishedRecipe recipe) {
        ICondition[] recipeConditions = conditions.toArray(ICondition[]::new);
        return new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject json) {
                recipe.serializeRecipeData(json);
                json.add("conditions", CraftingHelper.serialize(recipeConditions));
            }

            @Override
            public ResourceLocation getId() {
                return recipe.getId();
            }

            @Override
            public RecipeSerializer<?> getType() {
                return recipe.getType();
            }

            @Nullable
            @Override
            public JsonObject serializeAdvancement() {
                return recipe.serializeAdvancement();
            }

            @Nullable
            @Override
            public ResourceLocation getAdvancementId() {
                return recipe.getAdvancementId();
            }
        };
    }
}
