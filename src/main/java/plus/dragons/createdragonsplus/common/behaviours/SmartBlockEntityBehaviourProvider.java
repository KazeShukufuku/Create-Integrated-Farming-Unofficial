/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createdragonsplus.common.behaviours;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.List;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class SmartBlockEntityBehaviourProvider<T extends BlockEntity> extends SmartBlockEntity implements BehaviourProvider {
    protected final T blockEntity;

    public SmartBlockEntityBehaviourProvider(T blockEntity) {
        super(blockEntity.getType(), blockEntity.getBlockPos(), blockEntity.getBlockState());
        this.blockEntity = blockEntity;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Nullable
    @Override
    public Level getLevel() {
        return blockEntity.getLevel();
    }

    @Override
    public void setLevel(Level level) {
        blockEntity.setLevel(level);
    }
}
