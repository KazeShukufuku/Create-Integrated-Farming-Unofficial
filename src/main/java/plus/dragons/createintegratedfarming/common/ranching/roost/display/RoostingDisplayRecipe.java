/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package plus.dragons.createintegratedfarming.common.ranching.roost.display;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public record RoostingDisplayRecipe(
        ResourceLocation id,
        ResourceLocation representativeBlock,
        List<ResourceLocation> equivalentBlocks,
        IntRange productionTime,
        List<ItemFeedDisplay> itemFeeds,
        List<FluidFeedDisplay> fluidFeeds,
        List<OutputDisplay> outputs,
        LootDisplayStatus lootStatus) {
    private static final int MAX_BLOCKS = 64;
    static final int MAX_FEEDS = 4096;
    static final int MAX_OUTPUTS = 4096;

    public RoostingDisplayRecipe {
        equivalentBlocks = List.copyOf(equivalentBlocks);
        itemFeeds = List.copyOf(itemFeeds);
        fluidFeeds = List.copyOf(fluidFeeds);
        outputs = List.copyOf(outputs);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(id);
        buffer.writeResourceLocation(representativeBlock);
        writeList(buffer, equivalentBlocks, FriendlyByteBuf::writeResourceLocation);
        productionTime.encode(buffer);
        writeList(buffer, itemFeeds, (buf, value) -> value.encode(buf));
        writeList(buffer, fluidFeeds, (buf, value) -> value.encode(buf));
        writeList(buffer, outputs, (buf, value) -> value.encode(buf));
        buffer.writeEnum(lootStatus);
    }

    public static RoostingDisplayRecipe decode(FriendlyByteBuf buffer) {
        return new RoostingDisplayRecipe(
                buffer.readResourceLocation(),
                buffer.readResourceLocation(),
                readList(buffer, FriendlyByteBuf::readResourceLocation, MAX_BLOCKS),
                IntRange.decode(buffer),
                readList(buffer, ItemFeedDisplay::decode, MAX_FEEDS),
                readList(buffer, FluidFeedDisplay::decode, MAX_FEEDS),
                readList(buffer, OutputDisplay::decode, MAX_OUTPUTS),
                buffer.readEnum(LootDisplayStatus.class));
    }

    private static <T> void writeList(FriendlyByteBuf buffer, List<T> values, BiConsumer<FriendlyByteBuf, T> writer) {
        buffer.writeVarInt(values.size());
        values.forEach(value -> writer.accept(buffer, value));
    }

    private static <T> List<T> readList(FriendlyByteBuf buffer, Function<FriendlyByteBuf, T> reader, int maximum) {
        int size = buffer.readVarInt();
        if (size < 0 || size > maximum)
            throw new IllegalArgumentException("Invalid roosting display list size: " + size);
        var result = new ArrayList<T>(size);
        for (int i = 0; i < size; i++)
            result.add(reader.apply(buffer));
        return result;
    }

    public record IntRange(int minimum, int maximum) {
        public IntRange {
            if (minimum < 0 || maximum < minimum)
                throw new IllegalArgumentException("Invalid integer range: " + minimum + ".." + maximum);
        }

        public static IntRange exact(int value) { return new IntRange(value, value); }
        public boolean isExact() { return minimum == maximum; }
        public IntRange add(int value) { return new IntRange(Math.addExact(minimum, value), Math.addExact(maximum, value)); }
        public IntRange multiply(int value) { return new IntRange(Math.multiplyExact(minimum, value), Math.multiplyExact(maximum, value)); }
        private void encode(FriendlyByteBuf buffer) { buffer.writeVarInt(minimum); buffer.writeVarInt(maximum); }
        private static IntRange decode(FriendlyByteBuf buffer) { return new IntRange(buffer.readVarInt(), buffer.readVarInt()); }
    }

    public record ItemFeedDisplay(ItemStack ingredient, IntRange progress, IntRange cooldown, ItemStack remainder) {
        public ItemFeedDisplay { ingredient = ingredient.copy(); remainder = remainder.copy(); }
        private void encode(FriendlyByteBuf buffer) {
            buffer.writeItem(ingredient); progress.encode(buffer); cooldown.encode(buffer); buffer.writeItem(remainder);
        }
        private static ItemFeedDisplay decode(FriendlyByteBuf buffer) {
            return new ItemFeedDisplay(buffer.readItem(), IntRange.decode(buffer), IntRange.decode(buffer), buffer.readItem());
        }
    }

    public record FluidFeedDisplay(FluidStack ingredient, IntRange progress, IntRange cooldown) {
        public FluidFeedDisplay { ingredient = ingredient.copy(); }
        private void encode(FriendlyByteBuf buffer) {
            ingredient.writeToPacket(buffer); progress.encode(buffer); cooldown.encode(buffer);
        }
        private static FluidFeedDisplay decode(FriendlyByteBuf buffer) {
            return new FluidFeedDisplay(FluidStack.readFromPacket(buffer), IntRange.decode(buffer), IntRange.decode(buffer));
        }
    }

    public record OutputDisplay(ItemStack ingredient, IntRange count, boolean conditional) {
        public OutputDisplay { ingredient = ingredient.copy(); }
        private void encode(FriendlyByteBuf buffer) { buffer.writeItem(ingredient); count.encode(buffer); buffer.writeBoolean(conditional); }
        private static OutputDisplay decode(FriendlyByteBuf buffer) {
            return new OutputDisplay(buffer.readItem(), IntRange.decode(buffer), buffer.readBoolean());
        }
    }

    public enum LootDisplayStatus {
        EXACT, CONDITIONAL, COMPLEX, MISSING;

        public LootDisplayStatus merge(LootDisplayStatus other) {
            if (this == COMPLEX || other == COMPLEX) return COMPLEX;
            if (this == MISSING || other == MISSING) return MISSING;
            if (this == CONDITIONAL || other == CONDITIONAL) return CONDITIONAL;
            return EXACT;
        }
    }
}
