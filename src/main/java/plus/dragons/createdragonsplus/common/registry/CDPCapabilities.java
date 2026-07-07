/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createdragonsplus.common.registry;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import plus.dragons.createdragonsplus.common.behaviours.BehaviourProvider;

public class CDPCapabilities {
    public static final Capability<BehaviourProvider> BEHAVIOUR_PROVIDER = CapabilityManager.get(new CapabilityToken<>() {});
}
