/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.collectorsreap.farming.harvest;

import java.util.ArrayList;
import java.util.List;
import net.brdle.collectorsreap.common.block.CRBlocks;
import net.brdle.collectorsreap.common.block.BuddingDragonFruitBlock;
import net.brdle.collectorsreap.common.block.DragonFruitVineBlock;
import net.brdle.collectorsreap.common.block.FruitBushBlock;
import net.brdle.collectorsreap.common.block.TallBushCropBlock;
import net.brdle.collectorsreap.common.item.CRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.AreaCompatibleHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.farming.harvest.HarvestOperations;

public final class CollectorsReapHarvestBehaviour extends AreaCompatibleHarvestBehaviour {
    public static void register() {
        var behaviour = new CollectorsReapHarvestBehaviour();
        for (Block block : List.of(CRBlocks.LIME_BUSH.get(), CRBlocks.POMEGRANATE_BUSH.get(),
                CRBlocks.BUDDING_PINK_DRAGON_FRUIT_CROP.get(), CRBlocks.PINK_DRAGON_FRUIT_CROP.get(),
                CRBlocks.BULBOUS_ROSE_CROP.get(), CRBlocks.HEARTPETALS_CROP.get(),
                CRBlocks.DAMSELFLOWER.get(), CRBlocks.MOONTEAR.get(), CRBlocks.SKULL_LILY.get()))
            CustomHarvestBehaviour.REGISTRY.register(block, behaviour);
        // Portobello colonies already use the Farmer's Delight colony provider.
        // The three single-height sniffer seedlings already use CropBlock harvesting.
    }

    @Override
    public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
        if (!(context.level() instanceof ServerLevel))
            return false;
        if (state.getBlock() instanceof TallBushCropBlock crop)
            return harvestTallCrop(context, pos, state, crop);
        if (state.getBlock() instanceof DragonFruitVineBlock vine)
            return harvestVine(context, pos, state, vine);
        if (state.is(CRBlocks.BUDDING_PINK_DRAGON_FRUIT_CROP.get())) {
            // Its final budding stage is still a seedling, not a fruit harvest.
            var crop = (BuddingDragonFruitBlock) state.getBlock();
            if (!context.harvestPartiallyGrown() || state.getValue(crop.getAgeProperty()) == 0)
                return false;
            return HarvestOperations.harvestPlant(context, List.of(pos), pos,
                    state.setValue(crop.getAgeProperty(), 0), CRItems.DRAGON_FRUIT_SEEDS.get());
        }
        // These seedlings turn into a different, age-less flower block at maturity.
        String flower = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        Block seedling = BuiltInRegistries.BLOCK.get(new ResourceLocation("collectorsreap", flower + "_crop"));
        return HarvestOperations.harvestPlant(context, List.of(pos), pos,
                seedling.defaultBlockState(), seed(flower));
    }

    private boolean harvestTallCrop(AreaHarvestContext context, BlockPos pos, BlockState state, TallBushCropBlock crop) {
        BlockPos root = crop.isUpper(state) ? pos.below() : pos;
        if (!context.level().isLoaded(root))
            return false;
        BlockState lower = context.level().getBlockState(root);
        if (!crop.isLower(lower))
            return false;
        int age = lower.getValue(crop.getAgeProperty());
        if (age == 0 || age < crop.getMaxAge() && !context.harvestPartiallyGrown())
            return false;
        List<BlockPos> parts = new ArrayList<>();
        parts.add(root);
        if (crop.isTall(lower)) {
            if (!context.level().isLoaded(root.above()))
                return false;
            BlockState upper = context.level().getBlockState(root.above());
            if (!crop.isUpper(upper) || upper.getValue(crop.getAgeProperty()) != age)
                return false;
            parts.add(root.above());
        }
        if (crop instanceof FruitBushBlock bush && context.replant() && crop.isMaxAge(lower)) {
            if (!HarvestOperations.canHarvest(context, parts) || !context.claimHarvest(root))
                return false;
            // Keep both halves and the stunted flag; use the mod's special-fruit rules.
            BlockState picked = lower.setValue(crop.getAgeProperty(), crop.getMaxAge() - 2);
            context.level().setBlock(root, picked, Block.UPDATE_CLIENTS);
            context.level().setBlock(root.above(), picked.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER),
                    Block.UPDATE_CLIENTS);
            if (HarvestOperations.dropsEnabled(context)) {
                ItemStack fruit = bush.isSpecial(context.level(), root)
                        ? new ItemStack(bush.getSpecialFruit().orElseThrow())
                        : new ItemStack(bush.getFruit(), context.level().random.nextInt(1, 2 + bush.getMaxBonus()));
                context.collect(fruit);
            }
            return true;
        }
        Item seed = crop instanceof FruitBushBlock bush ? bush.getSeeds()
                : seed(BuiltInRegistries.BLOCK.getKey(crop).getPath().replace("_crop", ""));
        return HarvestOperations.harvestPlant(context, parts, root, crop.getStateForAge(0), seed);
    }

    private boolean harvestVine(AreaHarvestContext context, BlockPos pos, BlockState state, DragonFruitVineBlock vine) {
        int age = vine.getAge(state);
        if (age == 0 || age < vine.getMaxAge() && !context.harvestPartiallyGrown())
            return false;
        if (context.replant()) {
            // Ropelogged segments have no seed drops. Pick in place so neither the
            // rope nor the supporting vine is lost when any segment is harvested.
            ItemStack fruit = vine.isMaxAge(state)
                    ? new ItemStack(CRItems.PINK_DRAGON_FRUIT.get(), 1 + context.level().random.nextInt(2))
                    : ItemStack.EMPTY;
            return HarvestOperations.pickFruit(context, pos, state.setValue(vine.getAgeProperty(), 0), fruit);
        }
        List<BlockPos> parts = new ArrayList<>();
        List<BlockPos> ropes = new ArrayList<>();
        for (BlockPos part = pos; part.getY() < context.level().getMaxBuildHeight(); part = part.above()) {
            if (!context.level().isLoaded(part))
                return false;
            BlockState segment = context.level().getBlockState(part);
            if (!segment.is(vine))
                break;
            parts.add(part);
            if (segment.getValue(DragonFruitVineBlock.ROPELOGGED))
                ropes.add(part);
        }
        if (!HarvestOperations.harvestPlant(context, parts, pos, state, CRItems.DRAGON_FRUIT_SEEDS.get()))
            return false;
        for (BlockPos rope : ropes)
            DragonFruitVineBlock.destroyAndPlaceRope(context.level(), rope);
        return true;
    }

    private static Item seed(String crop) {
        return BuiltInRegistries.ITEM.get(new ResourceLocation("collectorsreap", crop + "_seeds"));
    }
}
