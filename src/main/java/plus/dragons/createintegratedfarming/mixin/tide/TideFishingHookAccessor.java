/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.mixin.tide;

import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import plus.dragons.createintegratedfarming.integration.ModIntegration.Mods;

@Mixin(TideFishingHook.class)
@Restriction(require = @Condition(Mods.TIDE))
public interface TideFishingHookAccessor {
    @Accessor(value = "openWater", remap = false)
    void createintegratedfarming$setOpenWater(boolean openWater);
}
