/* Copyright (C) 2025 DragonsPlus - SPDX-License-Identifier: LGPL-3.0-or-later */
package plus.dragons.createintegratedfarming.common.ranching.roost.display;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;

public record RoostingDisplaySnapshot(int revision, List<RoostingDisplayRecipe> recipes) {
    public static final RoostingDisplaySnapshot EMPTY = new RoostingDisplaySnapshot(-1, List.of());

    public RoostingDisplaySnapshot { recipes = List.copyOf(recipes); }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(revision);
        buffer.writeVarInt(recipes.size());
        recipes.forEach(recipe -> recipe.encode(buffer));
    }

    public static RoostingDisplaySnapshot decode(FriendlyByteBuf buffer) {
        int revision = buffer.readVarInt();
        int size = buffer.readVarInt();
        if (size < 0 || size > 256)
            throw new IllegalArgumentException("Invalid roosting recipe count: " + size);
        var recipes = new ArrayList<RoostingDisplayRecipe>(size);
        for (int i = 0; i < size; i++)
            recipes.add(RoostingDisplayRecipe.decode(buffer));
        return new RoostingDisplaySnapshot(revision, recipes);
    }
}
