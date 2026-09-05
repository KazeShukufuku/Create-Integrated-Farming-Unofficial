/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package plus.dragons.createintegratedfarming.common.ranching.roost;

import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHandlerWrapper;
import com.simibubi.create.foundation.item.ItemHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.common.ranching.roost.chicken.ChickenFood;
import plus.dragons.createintegratedfarming.common.registry.CIFChickenFoods;
import plus.dragons.createintegratedfarming.config.CIFConfig;

public abstract class AnimalRoostBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    public static final int DEFAULT_MINIMUM_PRODUCTION_TICKS = 6000;
    public static final int DEFAULT_MAXIMUM_PRODUCTION_TICKS = 11999;
    protected final ItemStackHandler inventory;
    public final IItemHandler outputHandler;
    protected int feedCooldown;
    protected int eggTime = productionCooldown();
    /** True when a ready production could not insert any item into the output inventory. */
    protected boolean outputInventoryBlocked;

    public int productionCooldown() {
        return 12000;
    }

    public int getEggTime() {
        return eggTime;
    }

    public int getFeedCooldown() {
        return feedCooldown;
    }

    public boolean isOutputInventoryBlocked() {
        return outputInventoryBlocked;
    }

    protected abstract ResourceLocation productionLootTable();

    protected SoundEvent feedingSound() {
        return SoundEvents.CHICKEN_AMBIENT;
    }

    public int feedFluid(FluidStack fluid, boolean simulate) {
        if (feedCooldown > 0 || eggTime <= 0)
            return 0;
        var food = CIFChickenFoods.getFluidFood(fluid.getFluid());
        if (food == null || fluid.getAmount() < food.amount())
            return 0;
        if (!simulate)
            feed(food);
        return food.amount();
    }

    public void feed(ChickenFood food) {
        assert level != null;
        applyFeeding(food.getProgress(level.random), food.getCooldown(level.random));
    }

    protected void applyFeeding(int progress, int cooldown) {
        assert level != null;
        eggTime = Math.max(0, eggTime - progress);
        feedCooldown = cooldown;
        level.playSound(null, worldPosition, feedingSound(), SoundSource.BLOCKS,
                1.0F, (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F);
        notifyUpdate();
    }

    /** Sound played when this roost successfully produces an item. */
    protected SoundEvent productionSound() {
        return SoundEvents.CHICKEN_EGG;
    }

    public AnimalRoostBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(20);
        this.inventory = new ItemStackHandler(CIFConfig.server().roostingInventorySlotCount.get()) {
            @Override
            public int getSlotLimit(int slot) {
                return CIFConfig.server().roostingInventorySlotSize.get();
            }

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                if (outputInventoryBlocked) {
                    outputInventoryBlocked = false;
                    if (level != null && !level.isClientSide)
                        notifyUpdate();
                }
            }
        };
        this.outputHandler = new ItemHandlerWrapper(inventory) {
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return stack;
            }
        };
    }

    public @Nullable IItemHandler getItemHandler(@Nullable Direction direction) {
        if (direction == getBlockState().getValue(HorizontalDirectionalBlock.FACING))
            return null;
        return outputHandler;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            IItemHandler handler = getItemHandler(side);
            if (handler != null)
                return LazyOptional.of(() -> handler).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this)
                .onlyInsertWhen(side -> side == getBlockState().getValue(HorizontalDirectionalBlock.FACING).getOpposite())
                .considerOccupiedWhen(side -> feedCooldown > 0)
                .setInsertionHandler(this::tryInsertFrom));
    }

    @Override
    public void initialize() {
        assert level != null;
        super.initialize();
        if (eggTime >= productionCooldown()) {
            eggTime = productionCooldown() / 2 + level.random.nextInt(productionCooldown() / 2);
        }
    }

    @Override
    public void lazyTick() {
        if (!(level instanceof ServerLevel serverLevel))
            return;
        boolean changed = false;
        if (feedCooldown > 0) {
            feedCooldown = Math.max(0, feedCooldown - lazyTickRate);
            changed = true;
        }
        if (eggTime > 0) {
            eggTime = Math.max(0, eggTime - lazyTickRate);
            changed = true;
        }
        if (eggTime <= 0) {
            boolean wasBlocked = outputInventoryBlocked;
            boolean inserted = false;
            var remainders = new ArrayList<ItemStack>();
            var lootTable = serverLevel.getServer().getLootData().getLootTable(productionLootTable());
            var lootParams = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.BLOCK_STATE, getBlockState())
                    .withParameter(LootContextParams.ORIGIN, worldPosition.getCenter())
                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, this)
                    .create(LootContextParamSets.BLOCK);
            var lootStacks = lootTable.getRandomItems(lootParams);
            for (var stack : lootStacks) {
                ItemStack remainder = ItemHandlerHelper.insertItem(inventory, stack, false);
                inserted |= stack.getCount() != remainder.getCount();
                if (!remainder.isEmpty())
                    remainders.add(remainder);
            }
            if (inserted) {
                outputInventoryBlocked = false;
                for (ItemStack remainder : remainders)
                    Containers.dropItemStack(
                            serverLevel,
                            worldPosition.getX() + .5,
                            worldPosition.getY() + .5,
                            worldPosition.getZ() + .5,
                            remainder);
                eggTime = 6000 + level.random.nextInt(6000);
                level.playSound(
                        null, worldPosition, productionSound(), SoundSource.BLOCKS,
                        1.0F, (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F);
                changed = true;
            } else {
                outputInventoryBlocked = !lootStacks.isEmpty();
            }
            changed |= wasBlocked != outputInventoryBlocked;
        }
        if (changed)
            notifyUpdate();
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("Inventory", inventory.serializeNBT());
        tag.putInt("EggLayTime", eggTime);
        tag.putInt("FeedCooldown", feedCooldown);
        tag.putBoolean("OutputInventoryBlocked", outputInventoryBlocked);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        eggTime = Mth.clamp(tag.getInt("EggLayTime"), 0, 12000);
        feedCooldown = tag.getInt("FeedCooldown");
        outputInventoryBlocked = tag.getBoolean("OutputInventoryBlocked");
    }

    @Override
    public void destroy() {
        super.destroy();
        ItemHelper.dropContents(level, worldPosition, inventory);
    }

    protected ItemStack tryInsertFrom(TransportedItemStack transported, Direction side, boolean simulate) {
        assert level != null;
        ItemStack stack = transported.stack.copy();
        if (feedItem(stack, simulate)) {
            if (!simulate) stack.shrink(1);
        }
        return stack;
    }

    public abstract boolean feedItem(ItemStack stack, boolean simulate);

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (eggTime > 0) {
            int seconds = (eggTime + 19) / 20;
            String remaining = String.format("%d:%02d", seconds / 60, seconds % 60);
            addGoggleLine(tooltip, "goggles.roost.next_output", ChatFormatting.GRAY, remaining);
        } else if (outputInventoryBlocked) {
            addGoggleLine(tooltip, "goggles.roost.output_inventory_full", ChatFormatting.RED);
        } else {
            addGoggleLine(tooltip, "goggles.roost.ready", ChatFormatting.GOLD);
        }
        return true;
    }

    private static void addGoggleLine(List<Component> tooltip, String key, ChatFormatting color, Object... args) {
        new net.createmod.catnip.lang.LangBuilder("create_integrated_farming")
                .translate(key, args)
                .style(color)
                .forGoggles(tooltip);
    }
}
