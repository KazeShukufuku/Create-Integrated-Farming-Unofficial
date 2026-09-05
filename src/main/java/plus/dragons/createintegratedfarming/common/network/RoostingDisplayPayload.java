/* Copyright (C) 2025 DragonsPlus - SPDX-License-Identifier: LGPL-3.0-or-later */
package plus.dragons.createintegratedfarming.common.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplaySnapshot;

public record RoostingDisplayPayload(RoostingDisplaySnapshot snapshot) {
    public void encode(FriendlyByteBuf buffer) { snapshot.encode(buffer); }
    public static RoostingDisplayPayload decode(FriendlyByteBuf buffer) {
        return new RoostingDisplayPayload(RoostingDisplaySnapshot.decode(buffer));
    }
    public static void handle(RoostingDisplayPayload payload, Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> RoostingDisplayClientCache.accept(payload.snapshot()));
        context.setPacketHandled(true);
    }
}
