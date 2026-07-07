/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createdragonsplus.common.behaviours;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import java.util.Collection;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import plus.dragons.createdragonsplus.util.MethodsReturnNullabilityUnknownByDefault;
import plus.dragons.createdragonsplus.util.ParametersNullabilityUnknownByDefault;

@AutoRegisterCapability
@MethodsReturnNullabilityUnknownByDefault
@ParametersNullabilityUnknownByDefault
public interface BehaviourProvider {
    <T extends BlockEntityBehaviour> T getBehaviour(BehaviourType<T> type);

    Collection<BlockEntityBehaviour> getAllBehaviours();

    static BehaviourProvider of(SmartBlockEntity blockEntity) {
        return new BehaviourProvider() {
            @Override
            public <T extends BlockEntityBehaviour> T getBehaviour(BehaviourType<T> type) {
                return blockEntity.getBehaviour(type);
            }

            @Override
            public Collection<BlockEntityBehaviour> getAllBehaviours() {
                return blockEntity.getAllBehaviours();
            }
        };
    }
}
