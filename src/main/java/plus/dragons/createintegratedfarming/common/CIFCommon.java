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

package plus.dragons.createintegratedfarming.common;

import com.simibubi.create.foundation.item.ItemDescription;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.dragons.createdragonsplus.common.CDPRegistrate;
import plus.dragons.createintegratedfarming.common.registry.CIFArmInteractionPoints;
import plus.dragons.createintegratedfarming.common.registry.CIFBlockEntities;
import plus.dragons.createintegratedfarming.common.registry.CIFBlockSpoutingBehaviours;
import plus.dragons.createintegratedfarming.common.registry.CIFBlocks;
import plus.dragons.createintegratedfarming.common.registry.CIFCreativeModeTabs;
import plus.dragons.createintegratedfarming.common.registry.CIFChickenFoods;
import plus.dragons.createintegratedfarming.common.registry.CIFRoostCapturables;
import plus.dragons.createintegratedfarming.config.CIFConfig;
import plus.dragons.createintegratedfarming.integration.ModIntegration;
import plus.dragons.createintegratedfarming.integration.crabbersdelight.registry.CrabbersDelightArmInteractionPointTypes;
import plus.dragons.createintegratedfarming.integration.delightoflight.registry.DelightOFlightHarvestBehaviors;
import plus.dragons.createintegratedfarming.integration.farmersdelight.registry.FDBlockSpoutingBehaviours;
import plus.dragons.createintegratedfarming.integration.farmersdelight.registry.FDHarvestBehaviours;
import plus.dragons.createintegratedfarming.integration.mynethersdelight.registry.MNDArmInteractionPointTypes;
import plus.dragons.createintegratedfarming.integration.mynethersdelight.registry.MNDBlockSpoutingBehaviors;
import plus.dragons.createintegratedfarming.integration.netherdepthupgrade.registry.NDUBlocks;
import plus.dragons.createintegratedfarming.integration.ranching.DynamicBirdRoosts;
import plus.dragons.createintegratedfarming.integration.twilightdelight.registry.TwilightDelightArmInteractionPointTypes;
import plus.dragons.createintegratedfarming.integration.twilightdelight.registry.TwilightDelightHarvestBehaviours;
import plus.dragons.createintegratedfarming.integration.untitledduck.registry.UntitledDuckBlockEntities;
import plus.dragons.createintegratedfarming.integration.untitledduck.registry.UntitledDuckBlocks;
import plus.dragons.createintegratedfarming.integration.untitledduck.registry.UntitledDuckCapturables;

public class CIFCommon {
    public static final String ID = "create_integrated_farming";
    public static final Logger LOGGER = LoggerFactory.getLogger("Create: Integrated Farming");
    public static final CDPRegistrate REGISTRATE = new CDPRegistrate(ID)
            .setTooltipModifier(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE));

    public static void init(IEventBus modBus) {
        REGISTRATE.registerEventListeners(modBus);
        CIFCreativeModeTabs.register(modBus);
        CIFBlocks.register(modBus);
        if (ModIntegration.UNTITLED_DUCK.enabled())
            UntitledDuckBlocks.register(modBus);
        CIFBlockEntities.register(modBus);
        if (ModIntegration.UNTITLED_DUCK.enabled())
            UntitledDuckBlockEntities.register(modBus);
        if (ModIntegration.MY_NETHERS_DELIGHT.enabled())
            MNDArmInteractionPointTypes.register();
        if (ModIntegration.CRABBERS_DELIGHT.enabled())
            CrabbersDelightArmInteractionPointTypes.register();
        if (ModIntegration.TWILIGHT_DELIGHT.enabled())
            TwilightDelightArmInteractionPointTypes.register();
        CIFArmInteractionPoints.register(modBus);
        if (ModIntegration.NETHER_DEPTHS_UPGRADE.enabled())
            NDUBlocks.register();
        if (ModIntegration.ENVIRONMENTAL.enabled())
            DynamicBirdRoosts.registerEnvironmental(modBus);
        if (ModIntegration.AUTUMNITY.enabled())
            DynamicBirdRoosts.registerAutumnity(modBus);
        modBus.register(CIFCommon.class);
        modBus.register(new CIFConfig());
        MinecraftForge.EVENT_BUS.addListener(CIFChickenFoods::addReloadListeners);
    }

    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CIFBlockSpoutingBehaviours::register);
        if (ModIntegration.FARMERS_DELIGHT.enabled())
            event.enqueueWork(FDBlockSpoutingBehaviours::register);
        if (ModIntegration.FARMERS_DELIGHT.enabled())
            event.enqueueWork(FDHarvestBehaviours::register);
        if (ModIntegration.MY_NETHERS_DELIGHT.enabled())
            event.enqueueWork(MNDBlockSpoutingBehaviors::register);
        if (ModIntegration.DELIGHT_O_FLIGHT.enabled())
            event.enqueueWork(DelightOFlightHarvestBehaviors::register);
        if (ModIntegration.TWILIGHT_DELIGHT.enabled())
            event.enqueueWork(TwilightDelightHarvestBehaviours::register);
        if (ModIntegration.CULTURAL_DELIGHTS.enabled())
            event.enqueueWork(CIFCommon::registerCulturalDelightsHarvests);
        if (ModIntegration.HEARTH_AND_HARVEST.enabled()
                || ModIntegration.WINDSWEPT.enabled()
                || ModIntegration.FESTIVE_DELIGHT.enabled()
                || ModIntegration.NETHERS_EXOTICISM.enabled()
                || ModIntegration.CORN_DELIGHT.enabled())
            event.enqueueWork(plus.dragons.createintegratedfarming.integration.RegistryHarvestBehaviours::register);
        event.enqueueWork(CIFRoostCapturables::register);
        if (ModIntegration.UNTITLED_DUCK.enabled())
            event.enqueueWork(UntitledDuckCapturables::register);
        if (ModIntegration.ENVIRONMENTAL.enabled())
            event.enqueueWork(DynamicBirdRoosts::registerEnvironmentalCapturable);
        if (ModIntegration.AUTUMNITY.enabled())
            event.enqueueWork(DynamicBirdRoosts::registerAutumnityCapturable);
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(ID, path);
    }

    private static void registerCulturalDelightsHarvests() {
        try {
            Class.forName("plus.dragons.createintegratedfarming.integration.culturaldelights.CulturalDelightsIntegration")
                    .getMethod("register")
                    .invoke(null);
        } catch (ReflectiveOperationException exception) {
            LOGGER.error("Failed to register Cultural Delights harvesting compatibility", exception);
        }
    }

}
