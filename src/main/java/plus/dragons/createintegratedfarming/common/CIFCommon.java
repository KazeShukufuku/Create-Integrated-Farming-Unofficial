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
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plus.dragons.createdragonsplus.common.CDPRegistrate;
import plus.dragons.createintegratedfarming.common.farming.harvest.GlowBerryHarvestBehaviour;
import plus.dragons.createintegratedfarming.common.registry.CIFArmInteractionPoints;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetCatchProviders;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetEntityCaptures;
import plus.dragons.createintegratedfarming.common.fishing.net.FishingNetMedium;
import plus.dragons.createintegratedfarming.common.network.CIFPackets;
import plus.dragons.createintegratedfarming.common.ranching.roost.display.RoostingDisplaySync;
import plus.dragons.createintegratedfarming.common.registry.CIFBlockEntities;
import plus.dragons.createintegratedfarming.common.registry.CIFBlockSpoutingBehaviours;
import plus.dragons.createintegratedfarming.common.registry.CIFBlocks;
import plus.dragons.createintegratedfarming.common.registry.CIFCreativeModeTabs;
import plus.dragons.createintegratedfarming.common.registry.CIFChickenFoods;
import plus.dragons.createintegratedfarming.common.registry.CIFRoostCapturables;
import plus.dragons.createintegratedfarming.common.registry.CIFRoostingDisplayProfiles;
import plus.dragons.createintegratedfarming.config.CIFConfig;
import plus.dragons.createintegratedfarming.integration.ModIntegration;
import plus.dragons.createintegratedfarming.integration.endersdelight.farming.harvest.EndersHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.hauntedharvest.farming.harvest.CornHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.netherexp.farming.harvest.CerebrageHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.netherexp.farming.harvest.SorrowsquashHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.netherexp.farming.harvest.WarpedWartHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.supplementaries.farming.harvest.FlaxHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.atmospheric.farming.harvest.AloeHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.atmospheric.farming.harvest.AtmosphericFruitHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.neapolitan.farming.harvest.MintHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.crabbersdelight.registry.CrabbersDelightArmInteractionPointTypes;
import plus.dragons.createintegratedfarming.integration.delightoflight.registry.DelightOFlightHarvestBehaviors;
import plus.dragons.createintegratedfarming.integration.farmersdelight.registry.FDBlockSpoutingBehaviours;
import plus.dragons.createintegratedfarming.integration.farmersdelight.registry.FDHarvestBehaviours;
import plus.dragons.createintegratedfarming.integration.mynethersdelight.registry.MNDArmInteractionPointTypes;
import plus.dragons.createintegratedfarming.integration.mynethersdelight.farming.harvest.PowderyHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.mynethersdelight.registry.MNDBlockSpoutingBehaviors;
import plus.dragons.createintegratedfarming.integration.netherdepthupgrade.fishing.NDUFishingNetCatchProvider;
import plus.dragons.createintegratedfarming.integration.tide.TideIntegration;
import plus.dragons.createintegratedfarming.integration.upgradeaquatic.farming.harvest.MulberryHarvestBehaviour;
import plus.dragons.createintegratedfarming.integration.vanillabackport.VanillaBackportIntegration;
import plus.dragons.createintegratedfarming.integration.vanillabackport.registry.VanillaBackportRoostCapturables;
import plus.dragons.createintegratedfarming.integration.vanillabackport.registry.VanillaBackportRoostingDisplayProfiles;
import plus.dragons.createintegratedfarming.integration.ranching.DynamicBirdRoosts;
import plus.dragons.createintegratedfarming.integration.autumnity.registry.AutumnityRoostingDisplayProfiles;
import plus.dragons.createintegratedfarming.integration.environmental.registry.EnvironmentalRoostingDisplayProfiles;
import plus.dragons.createintegratedfarming.integration.twilightdelight.registry.TwilightDelightArmInteractionPointTypes;
import plus.dragons.createintegratedfarming.integration.twilightdelight.registry.TwilightDelightHarvestBehaviours;
import plus.dragons.createintegratedfarming.integration.untitledduck.registry.UntitledDuckBlockEntities;
import plus.dragons.createintegratedfarming.integration.untitledduck.registry.UntitledDuckBlocks;
import plus.dragons.createintegratedfarming.integration.untitledduck.registry.UntitledDuckCapturables;
import plus.dragons.createintegratedfarming.integration.untitledduck.registry.UntitledDuckRoostingDisplayProfiles;

public class CIFCommon {
    public static final String ID = "create_integrated_farming";
    public static final Logger LOGGER = LoggerFactory.getLogger("Create: Integrated Farming");
    public static final CDPRegistrate REGISTRATE = new CDPRegistrate(ID)
            .setTooltipModifier(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE));

    public static void init(IEventBus modBus) {
        REGISTRATE.registerEventListeners(modBus);
        CIFPackets.register();
        RoostingDisplaySync.register();
        CIFCreativeModeTabs.register(modBus);
        CIFBlocks.register(modBus);
        if (ModIntegration.VANILLA_BACKPORT.enabled())
            VanillaBackportIntegration.register(modBus);
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
        FishingNetCatchProviders.register(asResource("vanilla"), FishingNetMedium.WATER, context ->
                context.level().getServer().getLootData().getLootTable(BuiltInLootTables.FISHING)
                        .getRandomItems(context.lootParams()));
        if (ModIntegration.NETHER_DEPTHS_UPGRADE.enabled()) {
            FishingNetCatchProviders.register(
                    ModIntegration.NETHER_DEPTHS_UPGRADE.asResource("fishing_net"),
                    FishingNetMedium.LAVA,
                    new NDUFishingNetCatchProvider());
            FishingNetEntityCaptures.register(com.scouter.netherdepthsupgrade.entity.LavaAnimal.class::isInstance);
        }
        if (ModIntegration.TIDE.enabled())
            TideIntegration.register();
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
        event.enqueueWork(GlowBerryHarvestBehaviour::register);
        event.enqueueWork(CIFBlockSpoutingBehaviours::register);
        if (ModIntegration.NETHER_EXPANSION.enabled()) {
            event.enqueueWork(WarpedWartHarvestBehaviour::register);
            event.enqueueWork(CerebrageHarvestBehaviour::register);
            event.enqueueWork(SorrowsquashHarvestBehaviour::register);
        }
        if (ModIntegration.HAUNTED_HARVEST.enabled())
            event.enqueueWork(CornHarvestBehaviour::register);
        if (ModIntegration.ENDERS_DELIGHT.enabled())
            event.enqueueWork(EndersHarvestBehaviour::register);
        if (ModIntegration.UPGRADE_AQUATIC.enabled())
            event.enqueueWork(MulberryHarvestBehaviour::register);
        if (ModIntegration.SUPPLEMENTARIES.enabled())
            event.enqueueWork(FlaxHarvestBehaviour::register);
        if (ModIntegration.ATMOSPHERIC.enabled()) {
            event.enqueueWork(AloeHarvestBehaviour::register);
            event.enqueueWork(AtmosphericFruitHarvestBehaviour::register);
        }
        if (ModIntegration.NEAPOLITAN.enabled())
            event.enqueueWork(MintHarvestBehaviour::register);
        if (ModIntegration.FARMERS_DELIGHT.enabled())
            event.enqueueWork(FDBlockSpoutingBehaviours::register);
        if (ModIntegration.FARMERS_DELIGHT.enabled())
            event.enqueueWork(FDHarvestBehaviours::register);
        if (ModIntegration.MY_NETHERS_DELIGHT.enabled())
            event.enqueueWork(MNDBlockSpoutingBehaviors::register);
        if (ModIntegration.MY_NETHERS_DELIGHT.enabled())
            event.enqueueWork(PowderyHarvestBehaviour::register);
        if (ModIntegration.DELIGHT_O_FLIGHT.enabled())
            event.enqueueWork(DelightOFlightHarvestBehaviors::register);
        if (ModIntegration.TWILIGHT_DELIGHT.enabled())
            event.enqueueWork(TwilightDelightHarvestBehaviours::register);
        if (ModIntegration.CULTURAL_DELIGHTS.enabled())
            event.enqueueWork(CIFCommon::registerCulturalDelightsHarvests);
        if (ModIntegration.AUTUMNITY.enabled()
                || ModIntegration.HEARTH_AND_HARVEST.enabled()
                || ModIntegration.WINDSWEPT.enabled()
                || ModIntegration.FESTIVE_DELIGHT.enabled()
                || ModIntegration.NETHERS_EXOTICISM.enabled()
                || ModIntegration.CORN_DELIGHT.enabled())
            event.enqueueWork(plus.dragons.createintegratedfarming.integration.RegistryHarvestBehaviours::register);
        event.enqueueWork(CIFRoostCapturables::register);
        event.enqueueWork(CIFRoostingDisplayProfiles::register);
        if (ModIntegration.VANILLA_BACKPORT.enabled())
            event.enqueueWork(VanillaBackportRoostCapturables::register);
        if (ModIntegration.VANILLA_BACKPORT.enabled())
            event.enqueueWork(VanillaBackportRoostingDisplayProfiles::register);
        if (ModIntegration.UNTITLED_DUCK.enabled())
            event.enqueueWork(UntitledDuckCapturables::register);
        if (ModIntegration.UNTITLED_DUCK.enabled())
            event.enqueueWork(UntitledDuckRoostingDisplayProfiles::register);
        if (ModIntegration.ENVIRONMENTAL.enabled())
            event.enqueueWork(DynamicBirdRoosts::registerEnvironmentalCapturable);
        if (ModIntegration.ENVIRONMENTAL.enabled())
            event.enqueueWork(EnvironmentalRoostingDisplayProfiles::register);
        if (ModIntegration.AUTUMNITY.enabled())
            event.enqueueWork(DynamicBirdRoosts::registerAutumnityCapturable);
        if (ModIntegration.AUTUMNITY.enabled())
            event.enqueueWork(AutumnityRoostingDisplayProfiles::register);
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
