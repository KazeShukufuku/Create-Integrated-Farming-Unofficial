/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.mixin;

import java.util.List;
import java.util.Set;
import me.fallenbreath.conditionalmixin.api.mixin.RestrictiveMixinConfigPlugin;
import org.jetbrains.annotations.Nullable;

public class UniversalConditionalMixinPlugin extends RestrictiveMixinConfigPlugin {
    @Override
    @Nullable
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    @Nullable
    public List<String> getMixins() {
        return null;
    }
}
