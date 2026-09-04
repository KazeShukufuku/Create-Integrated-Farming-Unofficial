/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.common.fishing.net;

import java.util.List;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface FishingNetCatchProvider {
    List<ItemStack> getCatch(FishingNetCatchContext context);
}
