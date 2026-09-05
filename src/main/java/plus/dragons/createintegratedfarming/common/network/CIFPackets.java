/* Copyright (C) 2025 DragonsPlus - SPDX-License-Identifier: LGPL-3.0-or-later */
package plus.dragons.createintegratedfarming.common.network;

import java.util.Optional;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import plus.dragons.createintegratedfarming.common.CIFCommon;

public final class CIFPackets {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(CIFCommon.asResource("main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private CIFPackets() {}

    public static void register() {
        CHANNEL.registerMessage(0, RoostingDisplayPayload.class,
                RoostingDisplayPayload::encode, RoostingDisplayPayload::decode,
                RoostingDisplayPayload::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
