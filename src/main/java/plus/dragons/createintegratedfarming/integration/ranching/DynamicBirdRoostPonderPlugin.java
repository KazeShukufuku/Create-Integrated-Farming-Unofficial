/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.integration.ranching;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import plus.dragons.createintegratedfarming.client.ponder.CIFPonderPlugin;
import plus.dragons.createintegratedfarming.client.ponder.CIFPonderTags;
import plus.dragons.createintegratedfarming.client.ponder.scene.RoostScene;

/** Ponder registration shared by the optional Environmental and Autumnity roosts. */
public final class DynamicBirdRoostPonderPlugin {
    private DynamicBirdRoostPonderPlugin() {}

    public static void registerEnvironmental() {
        register(DynamicBirdRoosts::environmentalDuck);
    }

    public static void registerAutumnity() {
        register(DynamicBirdRoosts::autumnityTurkey);
    }

    private static void register(java.util.function.Supplier<net.minecraft.world.level.block.Block> roost) {
        CIFPonderPlugin.TAGS.add(helper -> registerTags(helper, roost.get()));
        CIFPonderPlugin.SCENES.add(helper -> registerScenes(helper, roost.get()));
    }

    private static void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper, net.minecraft.world.level.block.Block roost) {
        helper.addToTag(CIFPonderTags.RANCHING_APPLIANCES).add(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(roost));
        helper.addToTag(AllCreatePonderTags.ARM_TARGETS).add(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(roost));
    }

    private static void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper, net.minecraft.world.level.block.Block roost) {
        helper.forComponents(net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(roost))
                .addStoryBoard("roost/operate", RoostScene::operate, CIFPonderTags.RANCHING_APPLIANCES, AllCreatePonderTags.ARM_TARGETS);
    }
}
