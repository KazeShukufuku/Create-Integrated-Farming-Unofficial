/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.common.fishing.net;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class LavaFishingNetMovementBehaviour extends AbstractFishingNetMovementBehaviour<LavaFishingNetContext> {
    @Override
    public LavaFishingNetContext createFishingNetContext(ServerLevel level) {
        return new LavaFishingNetContext(level, new ItemStack(Items.FISHING_ROD));
    }

    @Override
    protected LavaFishingNetContext getFishingNetContext(MovementContext context, ServerLevel level) {
        if (!(context.temporaryData instanceof LavaFishingNetContext))
            context.temporaryData = createFishingNetContext(level);
        return (LavaFishingNetContext) context.temporaryData;
    }
}
