/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package plus.dragons.createintegratedfarming.client;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import plus.dragons.createintegratedfarming.client.ponder.CIFPonderPlugin;
import plus.dragons.createintegratedfarming.common.CIFCommon;
import plus.dragons.createintegratedfarming.common.network.RoostingDisplayClientCache;
import plus.dragons.createintegratedfarming.integration.ModIntegration;
import plus.dragons.createintegratedfarming.integration.crabbersdelight.ponder.CrabbersDelightPonderPlugin;
import plus.dragons.createintegratedfarming.integration.delightoflight.ponder.DelightOFlightPonderPlugin;
import plus.dragons.createintegratedfarming.integration.farmersdelight.ponder.FDPonderPlugin;
import plus.dragons.createintegratedfarming.integration.mynethersdelight.ponder.MNDPonderPlugin;
import plus.dragons.createintegratedfarming.integration.netherdepthupgrade.ponder.NDUFishingNetPonderExample;
import plus.dragons.createintegratedfarming.integration.ranching.DynamicBirdRoostPonderPlugin;
import plus.dragons.createintegratedfarming.integration.twilightdelight.ponder.TwilightDelightPonderPlugin;
import plus.dragons.createintegratedfarming.integration.untitledduck.ponder.UntitledDuckPonderPlugin;
import plus.dragons.createintegratedfarming.integration.vanillabackport.registry.VanillaBackportBlocks;
import plus.dragons.createintegratedfarming.integration.tide.TideFishingNetPonderExample;

@Mod.EventBusSubscriber(modid = CIFCommon.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CIFClient {
    public static void construct() {
        // Register partial models before the first asynchronous resource reload starts.
        CIFPartialModels.init();
    }

    @SubscribeEvent
    public static void init(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(CIFClient::onLogout);
        PonderIndex.addPlugin(new CIFPonderPlugin());
        if (ModIntegration.FARMERS_DELIGHT.enabled())
            FDPonderPlugin.register();
        if (ModIntegration.MY_NETHERS_DELIGHT.enabled())
            MNDPonderPlugin.register();
        if (ModIntegration.NETHER_DEPTHS_UPGRADE.enabled())
            NDUFishingNetPonderExample.register();
        if (ModIntegration.TIDE.enabled())
            TideFishingNetPonderExample.register();
        if (ModIntegration.CRABBERS_DELIGHT.enabled())
            CrabbersDelightPonderPlugin.register();
        if (ModIntegration.UNTITLED_DUCK.enabled())
            UntitledDuckPonderPlugin.register();
        if (ModIntegration.DELIGHT_O_FLIGHT.enabled())
            DelightOFlightPonderPlugin.register();
        if (ModIntegration.TWILIGHT_DELIGHT.enabled())
            TwilightDelightPonderPlugin.register();
        if (ModIntegration.ENVIRONMENTAL.enabled())
            DynamicBirdRoostPonderPlugin.registerEnvironmental();
        if (ModIntegration.AUTUMNITY.enabled())
            DynamicBirdRoostPonderPlugin.registerAutumnity();
        if (ModIntegration.VANILLA_BACKPORT.enabled())
            CIFPonderPlugin.registerRoosts(VanillaBackportBlocks.CHICKEN_ROOST_WARM.getId(),
                    VanillaBackportBlocks.CHICKEN_ROOST_COLD.getId());
    }

    private static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        RoostingDisplayClientCache.clear();
    }
}
