/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.vanillabackport.ranching.roost;

import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import plus.dragons.createintegratedfarming.common.ranching.roost.chicken.ChickenRoostBlock;
import plus.dragons.createintegratedfarming.integration.vanillabackport.registry.VanillaBackportBlockEntities;

public class VanillaBackportChickenRoostBlock extends ChickenRoostBlock {
    public static final String VARIANT_TAG = "variant";
    public static final ResourceLocation WARM_VARIANT = new ResourceLocation("minecraft", "warm");
    public static final ResourceLocation COLD_VARIANT = new ResourceLocation("minecraft", "cold");

    private final ResourceLocation variant;

    public VanillaBackportChickenRoostBlock(
            Properties properties, Supplier<? extends Block> empty, ResourceLocation variant) {
        super(properties, empty);
        this.variant = variant;
    }

    @Override
    protected Chicken createChicken(Level level) {
        Chicken chicken = super.createChicken(level);
        CompoundTag tag = new CompoundTag();
        chicken.saveWithoutId(tag);
        tag.putString(VARIANT_TAG, variant.toString());
        chicken.load(tag);
        return chicken;
    }

    @Override
    public BlockEntityType<? extends VanillaBackportChickenRoostBlockEntity> getBlockEntityType() {
        return VanillaBackportBlockEntities.CHICKEN_ROOST.get();
    }
}
