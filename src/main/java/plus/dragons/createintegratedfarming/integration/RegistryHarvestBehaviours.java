/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.autumnity.farming.harvest.FoulBerryHarvestBehaviour;

/**
 * Harvest adapters that only use registry ids and vanilla block-state properties.
 * This keeps optional 1.20 integrations binary-compatible across the Forge builds
 * of these mods, whose public implementation classes differ from their 1.21 ports.
 */
public final class RegistryHarvestBehaviours {
    private RegistryHarvestBehaviours() {}

    public static void register() {
        CustomHarvestBehaviour.REGISTRY.registerProvider(RegistryHarvestBehaviours::create);
    }

    private static @Nullable CustomHarvestBehaviour create(Block block) {
        String id = BuiltInRegistries.BLOCK.getKey(block).toString();
        return switch (id) {
            case "autumnity:foul_berry_bush", "autumnity:tall_foul_berry_bush" ->
                    new FoulBerryHarvestBehaviour();
            case "windswept:wild_berry_bush" -> new ResetCrop(block, "wild_berries", 3, 2, 1, 2);
            case "festivedelight:cinnamon_bushripe" -> new ReplaceCrop(block, "cinnamon_sticks", "festivedelight:cinnamon_bush", 1, 1);
            case "hearthandharvest:blueberry_bush" -> new ResetCrop(block, "blueberry", 3, 1, 2, 3);
            case "hearthandharvest:raspberry_bush" -> new ResetCrop(block, "raspberry", 3, 1, 2, 3);
            case "hearthandharvest:cotton" , "hearthandharvest:cotton_crop" -> new ResetCrop(block, "cotton", 7, 5, 1, 2);
            case "hearthandharvest:corn_stalk" -> new ResetCrop(block, "corn", 4, 3, 1, 2);
            case "hearthandharvest:grape_trellis" -> new GrapeTrellis(block);
            case "hearthandharvest:red_grapes", "hearthandharvest:green_grapes",
                    "hearthandharvest:budding_red_grapes", "hearthandharvest:budding_green_grapes" -> new GrapeVine(block);
            case "corn_delight:corn_crop" -> new HighCrop(block);
            case "nethersexoticism:jaboticaba_branch" -> new Jaboticaba(block);
            case "nethersexoticism:kiwano_leaves_stage_1" -> new BrokenFruit(block, false);
            case "nethersexoticism:bouddha_s_hand_block", "nethersexoticism:ramboutan_block" -> new BrokenFruit(block, true);
            case "nethersexoticism:pitaya_block", "nethersexoticism:pitaya_block_open" -> new Pitaya(block);
            default -> null;
        };
    }

    private static Item item(String namespace, String path) {
        return BuiltInRegistries.ITEM.get(new ResourceLocation(namespace, path));
    }

    private static @Nullable Integer number(BlockState state, String name) {
        Property<?> property = property(state, name);
        return property != null && state.getValue(property) instanceof Integer value ? value : null;
    }

    private static boolean flag(BlockState state, String name) {
        Property<?> property = property(state, name);
        return property != null && state.getValue(property) instanceof Boolean value && value;
    }

    private static @Nullable Property<?> property(BlockState state, String name) {
        for (Property<?> property : state.getProperties())
            if (property.getName().equals(name))
                return property;
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState set(BlockState state, String name, Comparable value) {
        Property property = property(state, name);
        return property == null || !property.getPossibleValues().contains(value) ? state : state.setValue(property, value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState copyCommonProperties(BlockState from, BlockState to) {
        for (Property property : to.getProperties()) {
            Property<?> source = property(from, property.getName());
            if (source != null && property.getPossibleValues().contains(from.getValue(source)))
                to = to.setValue(property, (Comparable) from.getValue(source));
        }
        return to;
    }

    private static void collect(HarvesterMovementBehaviour behaviour, MovementContext context, ItemStack stack) {
        if (!stack.isEmpty())
            behaviour.dropItem(context, stack);
    }

    private abstract static class StatefulHarvest implements CustomHarvestBehaviour {
        protected final Block block;

        protected StatefulHarvest(Block block) {
            this.block = block;
        }

        protected abstract boolean harvest(Level level, BlockPos pos, @Nullable Vec3 motion, java.util.function.Consumer<ItemStack> collector);

        @Override
        public final void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
            harvest(context.world, pos, context.motion, stack -> collect(behaviour, context, stack));
        }

        @Override
        public final boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
            return harvest(context.level(), pos, null, context::collect);
        }
    }

    private static class ResetCrop extends StatefulHarvest {
        private final String itemPath;
        private final int matureAge;
        private final int resetAge;
        private final int minimum;
        private final int maximum;

        private ResetCrop(Block block, String itemPath, int matureAge, int resetAge, int minimum, int maximum) {
            super(block);
            this.itemPath = itemPath;
            this.matureAge = matureAge;
            this.resetAge = resetAge;
            this.minimum = minimum;
            this.maximum = maximum;
        }

        @Override
        protected boolean harvest(Level level, BlockPos pos, @Nullable Vec3 motion, java.util.function.Consumer<ItemStack> collector) {
            BlockState state = level.getBlockState(pos);
            Integer age = number(state, "age");
            Item item = item(BuiltInRegistries.BLOCK.getKey(block).getNamespace(), itemPath);
            if (!state.is(block) || age == null || age < matureAge || item == Items.AIR)
                return false;
            collector.accept(new ItemStack(item, minimum + level.random.nextInt(maximum - minimum + 1)));
            level.setBlock(pos, set(state, "age", resetAge), 2);
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                    1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            return true;
        }
    }

    private static final class ReplaceCrop extends StatefulHarvest {
        private final String itemPath;
        private final ResourceLocation replacement;
        private final int minimum;
        private final int maximum;

        private ReplaceCrop(Block block, String itemPath, String replacement, int minimum, int maximum) {
            super(block);
            this.itemPath = itemPath;
            this.replacement = new ResourceLocation(replacement);
            this.minimum = minimum;
            this.maximum = maximum;
        }

        @Override
        protected boolean harvest(Level level, BlockPos pos, @Nullable Vec3 motion, java.util.function.Consumer<ItemStack> collector) {
            BlockState state = level.getBlockState(pos);
            Item item = item(BuiltInRegistries.BLOCK.getKey(block).getNamespace(), itemPath);
            Block replacementBlock = BuiltInRegistries.BLOCK.get(replacement);
            if (!state.is(block) || item == Items.AIR || replacementBlock == Blocks.AIR)
                return false;
            collector.accept(new ItemStack(item, minimum + level.random.nextInt(maximum - minimum + 1)));
            level.setBlock(pos, copyCommonProperties(state, replacementBlock.defaultBlockState()), 3);
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.NEUTRAL, 1.0F, 1.0F);
            return true;
        }
    }

    private static final class GrapeTrellis extends ResetCrop {
        private GrapeTrellis(Block block) {
            super(block, "red_grapes", 4, 0, 1, 2);
        }

        @Override
        protected boolean harvest(Level level, BlockPos pos, @Nullable Vec3 motion, java.util.function.Consumer<ItemStack> collector) {
            BlockState state = level.getBlockState(pos);
            Integer age = number(state, "age");
            Object plant = property(state, "plant") == null ? null : state.getValue(property(state, "plant"));
            if (!state.is(block) || age == null || age < 4 || plant == null || !plant.toString().toLowerCase().contains("grape"))
                return false;
            String fruit = plant.toString().toLowerCase().contains("red") ? "red_grapes" : "green_grapes";
            Item item = item("hearthandharvest", fruit);
            if (item == Items.AIR)
                return false;
            collector.accept(new ItemStack(item, 1 + level.random.nextInt(2)));
            level.setBlock(pos, set(state, "age", 0), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                    1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            return true;
        }
    }

    /** Hearth and Harvest 1.20.1's post-trellis grape crops use normal age states. */
    private static final class GrapeVine extends StatefulHarvest {
        private GrapeVine(Block block) {
            super(block);
        }

        @Override
        protected boolean harvest(Level level, BlockPos pos, @Nullable Vec3 motion, java.util.function.Consumer<ItemStack> collector) {
            BlockState state = level.getBlockState(pos);
            Integer age = number(state, "age");
            if (!state.is(block) || age == null || age < 3)
                return false;
            String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
            Item grapes = item("hearthandharvest", path.contains("red") ? "red_grapes" : "green_grapes");
            if (grapes == Items.AIR)
                return false;
            collector.accept(new ItemStack(grapes, 1 + level.random.nextInt(2)));
            level.setBlock(pos, set(state, "age", 0), 2);
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                    1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            return true;
        }
    }

    private static final class BrokenFruit implements CustomHarvestBehaviour {
        private final Block block;
        private final boolean preserveWater;

        private BrokenFruit(Block block, boolean preserveWater) {
            this.block = block;
            this.preserveWater = preserveWater;
        }

        @Override
        public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
            if (state.is(block))
                CustomHarvestBehaviour.harvestBlock(context.world, pos, replacement(state), null,
                        CustomHarvestBehaviour.getHarvestTool(context), 1.0F, stack -> behaviour.dropItem(context, stack));
        }

        @Override
        public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
            if (!state.is(block))
                return false;
            CustomHarvestBehaviour.harvestBlock(context.level(), pos, replacement(state), null,
                    context.tool(), 1.0F, context::collect);
            return true;
        }

        private BlockState replacement(BlockState state) {
            return preserveWater && flag(state, "waterlogged") ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }
    }

    private static final class Jaboticaba extends StatefulHarvest {
        private Jaboticaba(Block block) {
            super(block);
        }

        @Override
        protected boolean harvest(Level level, BlockPos pos, @Nullable Vec3 motion, java.util.function.Consumer<ItemStack> collector) {
            BlockState state = level.getBlockState(pos);
            Block empty = BuiltInRegistries.BLOCK.get(new ResourceLocation("nethersexoticism", "jaboticaba_branch_empty"));
            Item fruit = item("nethersexoticism", "jaboticaba");
            if (!state.is(block) || empty == Blocks.AIR || fruit == Items.AIR)
                return false;
            collector.accept(new ItemStack(fruit, level.random.nextFloat() < .75F ? 1 : 2));
            level.setBlock(pos, copyCommonProperties(state, empty.defaultBlockState()), 3);
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.NEUTRAL, 1.0F, 1.0F);
            return true;
        }
    }

    private static final class Pitaya implements CustomHarvestBehaviour {
        private final Block block;

        private Pitaya(Block block) {
            this.block = block;
        }

        @Override
        public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
            if (state.is(block))
                CustomHarvestBehaviour.harvestBlock(context.world, pos, replacement(state, context.motion), null,
                        CustomHarvestBehaviour.getHarvestTool(context), 1.0F, stack -> behaviour.dropItem(context, stack));
        }

        @Override
        public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
            if (!state.is(block))
                return false;
            CustomHarvestBehaviour.harvestBlock(context.level(), pos, replacement(state, null), null,
                    context.tool(), 1.0F, context::collect);
            return true;
        }

        private static BlockState replacement(BlockState state, @Nullable Vec3 motion) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            if (id.getPath().equals("pitaya_block_open"))
                return flag(state, "waterlogged") ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
            Block open = BuiltInRegistries.BLOCK.get(new ResourceLocation("nethersexoticism", "pitaya_block_open"));
            BlockState result = copyCommonProperties(state, open.defaultBlockState());
            Direction facing = facingFromMotion(motion);
            return facing == null ? result : set(result, "facing", facing);
        }

        private static @Nullable Direction facingFromMotion(@Nullable Vec3 motion) {
            if (motion == null || motion.x * motion.x + motion.z * motion.z < 1.0E-6)
                return null;
            Direction movement = Math.abs(motion.x) > Math.abs(motion.z)
                    ? motion.x > 0 ? Direction.EAST : Direction.WEST
                    : motion.z > 0 ? Direction.SOUTH : Direction.NORTH;
            return movement.getOpposite();
        }
    }

    private static final class HighCrop implements CustomHarvestBehaviour {
        private final Block block;

        private HighCrop(Block block) {
            this.block = block;
        }

        /**
         * Create Central Kitchen 1.20 already provides the contraption
         * harvester implementation for Corn Delight. Leave that path alone,
         * while retaining this behaviour's area-harvester support below.
         */
        @Override
        public boolean handlesMechanicalHarvester() {
            return !ModIntegration.CREATE_CENTRAL_KITCHEN.enabled();
        }

        @Override
        public void harvest(HarvesterMovementBehaviour behaviour, MovementContext context, BlockPos pos, BlockState state) {
            harvest(context.world, pos, CustomHarvestBehaviour.replant(), CustomHarvestBehaviour.partial(),
                    CustomHarvestBehaviour.getHarvestTool(context), stack -> behaviour.dropItem(context, stack), null);
        }

        @Override
        public boolean harvestInArea(AreaHarvestContext context, BlockPos pos, BlockState state) {
            return harvest(context.level(), pos, context.replant(), context.harvestPartiallyGrown(), context.tool(),
                    context::collect, context);
        }

        private boolean harvest(Level level, BlockPos pos, boolean replant, boolean partial, ItemStack tool,
                java.util.function.Consumer<ItemStack> collector, @Nullable AreaHarvestContext area) {
            BlockState state = level.getBlockState(pos);
            Integer age = number(state, "age");
            if (!state.is(block) || age == null) return false;
            int maximum = property(state, "age").getPossibleValues().stream().mapToInt(value -> (Integer) value).max().orElse(0);
            if (age < maximum && !partial) return false;
            if (!replant) {
                destroy(level, pos, state, tool, collector);
                return true;
            }
            int growUpperAge = growUpperAge(maximum);
            if (age < growUpperAge) return false;
            final boolean[] seedUsed = {false};
            CustomHarvestBehaviour.harvestBlock(level, pos, set(state, "age", growUpperAge), null, tool, 1.0F, stack -> {
                if (!seedUsed[0] && stack.is(block.asItem())) { stack.shrink(1); seedUsed[0] = true; }
                collector.accept(stack);
            });
            if (!seedUsed[0] && area != null)
                area.extractSeed(stack -> stack.is(block.asItem()), 1);
            return true;
        }

        private int growUpperAge(int fallback) {
            try {
                return (int) block.getClass().getMethod("getGrowUpperAge").invoke(block);
            } catch (ReflectiveOperationException ignored) {
                return fallback / 2;
            }
        }

        private void destroy(Level level, BlockPos pos, BlockState state, ItemStack tool, java.util.function.Consumer<ItemStack> collector) {
            if (flag(state, "upper")) {
                BlockState above = level.getBlockState(pos.above());
                if (above.is(block)) destroy(level, pos.above(), above, tool, collector);
            }
            CustomHarvestBehaviour.harvestBlock(level, pos, Blocks.AIR.defaultBlockState(), null, tool, 1.0F, collector);
        }
    }
}
