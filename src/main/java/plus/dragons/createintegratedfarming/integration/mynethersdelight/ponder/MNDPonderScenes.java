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

package plus.dragons.createintegratedfarming.integration.mynethersdelight.ponder;

import static com.soytutta.mynethersdelight.common.block.LetiosCompostBlock.FORGOTING;
import static plus.dragons.createintegratedfarming.client.ponder.scene.ArmPonderUtil.armTarget;

import com.simibubi.create.content.fluids.spout.SpoutBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint.Mode;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.soytutta.mynethersdelight.common.block.PowderyCaneBlock;
import com.soytutta.mynethersdelight.common.block.PowderyCannonBlock;
import com.soytutta.mynethersdelight.common.block.PowderyFlowerBlock;
import com.soytutta.mynethersdelight.common.registry.MNDBlocks;
import com.soytutta.mynethersdelight.common.registry.MNDItems;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import plus.dragons.createintegratedfarming.client.ponder.scene.ArmPonderUtil;

public class MNDPonderScenes {
    public static void chargingSoil(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("spout.catalyze_letios_compost", "Catalyzing Leteos Compost");
        scene.configureBasePlate(0, 0, 3);

        scene.world().modifyBlockEntity(util.grid().at(1, 3, 1), SpoutBlockEntity.class, be -> {
            var tank = be.getBehaviour(SmartFluidTankBehaviour.TYPE);
            tank.getPrimaryHandler().setFluid(new FluidStack(Fluids.LAVA, 1000));
        });
        scene.world().showSection(util.select().everywhere(), Direction.DOWN);
        var spout = util.select().position(1, 3, 1);
        var leteosCompost = util.grid().at(1, 1, 1);

        scene.overlay().showText(100)
                .text("Forgetting process of Leteos Compost can be speed up via Spout in ultra warm dimension")
                .pointAt(util.vector().centerOf(1, 3, 1))
                .placeNearTarget();

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(leteosCompost, bs -> bs.setValue(FORGOTING, 2), false);
        scene.idle(10);

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(leteosCompost, bs -> bs.setValue(FORGOTING, 4), false);
        scene.idle(10);

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(leteosCompost, bs -> bs.setValue(FORGOTING, 7), false);
        scene.idle(10);

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(leteosCompost, bs -> bs.setValue(FORGOTING, 9), false);
        scene.idle(10);

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(leteosCompost, bs -> bs.setValue(FORGOTING, 9), false);
        scene.idle(10);

        scene.world().modifyBlockEntityNBT(spout, SpoutBlockEntity.class, nbt -> nbt.putInt("ProcessingTicks", 20));
        scene.idle(20);
        scene.world().modifyBlock(leteosCompost, bs -> MNDBlocks.RESURGENT_SOIL.get().defaultBlockState(), false);
    }

    public static void harvestPowderyCrops(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("mechanical_arm.harvest_powdery_crops", "Harvesting Powdery Crops");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos cropRoot = util.grid().at(2, 0, 2);
        BlockPos cropBase = util.grid().at(2, 1, 2);
        BlockPos ripeCrop = util.grid().at(2, 5, 2);
        BlockPos armPos = util.grid().at(1, 1, 3);
        BlockPos outputDepot = util.grid().at(3, 1, 3);
        var cropColumn = util.select().fromTo(cropRoot, ripeCrop);
        var arm = util.select().position(armPos);
        var output = util.select().position(outputDepot);
        var armAndOutput = util.select().position(armPos).add(output);

        scene.world().modifyBlocks(util.select().everywhere(), MNDPonderScenes::makePowderyCropSafe, false);
        rebuildPowderyCrop(scene, cropRoot, cropBase, ripeCrop);
        ArmPonderUtil.configureArm(scene, util, armPos,
                armTarget(new ResourceLocation("create_integrated_farming", "powdery_cannon"), cropBase, Mode.TAKE),
                armTarget(new ResourceLocation("create", "depot"), outputDepot, Mode.DEPOSIT));
        scene.world().showSection(cropColumn, Direction.UP);
        scene.idle(10);

        scene.overlay().showText(60)
                .text("Mechanical Arms can harvest Powdery Crops")
                .pointAt(util.vector().centerOf(armPos))
                .placeNearTarget()
                .attachKeyFrame();
        scene.world().showSection(armAndOutput, Direction.DOWN);
        scene.idle(70);

        scene.overlay().showText(70)
                .text("The arm scans up to 7 blocks upward from the crop block selected as its target")
                .pointAt(util.vector().centerOf(cropBase))
                .placeNearTarget()
                .attachKeyFrame();
        scene.overlay().showOutline(PonderPalette.INPUT, cropBase, util.select().position(cropBase), 40);
        scene.overlay().showOutline(PonderPalette.OUTPUT, output, output, 90);
        scene.idle(50);
        scene.overlay().showOutline(PonderPalette.INPUT, cropColumn, cropColumn, 40);
        scene.idle(40);

        scene.overlay().showText(70)
                .text("When a ripe part is found, it collects Bullet Peppers without breaking the plant")
                .pointAt(util.vector().centerOf(ripeCrop))
                .placeNearTarget()
                .attachKeyFrame();
        scene.idle(10);
        scene.world().setKineticSpeed(arm, 64);
        var pepper = new ItemStack(MNDItems.BULLET_PEPPER.get());
        scene.idle(20);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(24);
        scene.world().modifyBlock(ripeCrop, MNDPonderScenes::makePowderyCropSafe, false);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.SEARCH_OUTPUTS, pepper, -1);
        scene.idle(20);
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, pepper, 0);
        scene.idle(24);
        scene.world().modifyBlockEntity(outputDepot, DepotBlockEntity.class, depot -> depot.setHeldItem(pepper));
        scene.world().instructArm(armPos, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);
    }

    private static BlockState makePowderyCropSafe(BlockState state) {
        if (state.hasProperty(PowderyCaneBlock.LIT)) {
            state = state.setValue(PowderyCaneBlock.LIT, false);
        }
        if (state.hasProperty(PowderyCaneBlock.PRESSURE)) {
            state = state.setValue(PowderyCaneBlock.PRESSURE, 0);
        }
        if (state.hasProperty(PowderyCannonBlock.LIT)) {
            state = state.setValue(PowderyCannonBlock.LIT, false);
        }
        if (state.hasProperty(PowderyCannonBlock.PRESSURE)) {
            state = state.setValue(PowderyCannonBlock.PRESSURE, 0);
        }
        if (state.hasProperty(PowderyFlowerBlock.LIT)) {
            state = state.setValue(PowderyFlowerBlock.LIT, false);
        }
        if (state.hasProperty(PowderyFlowerBlock.PRESSURE)) {
            state = state.setValue(PowderyFlowerBlock.PRESSURE, 0);
        }
        return state;
    }

    private static void rebuildPowderyCrop(CreateSceneBuilder scene, BlockPos cropRoot, BlockPos cropBase, BlockPos ripeCrop) {
        scene.world().setBlock(cropRoot, powderyCannon(false, BambooLeaves.LARGE, 1), false);
        scene.world().setBlock(cropBase, powderyCane(2, false, false), false);
        scene.world().setBlock(cropBase.above(), powderyCane(2, false, true), false);
        scene.world().setBlock(cropBase.above(2), powderyCane(2, false, false), false);
        scene.world().setBlock(cropBase.above(3), powderyCane(2, false), false);
        scene.world().setBlock(ripeCrop, bulletPepper(2, true), false);
    }

    private static BlockState powderyCannon(boolean lit, BambooLeaves leaves, int stage) {
        var state = MNDBlocks.POWDERY_CANNON.get().defaultBlockState();
        state = setLeaves(state, leaves);
        state = setStage(state, stage);
        return makePowderyCropReady(state, lit);
    }

    private static BlockState powderyCane(int age, boolean lit) {
        return powderyCane(age, lit, false);
    }

    private static BlockState powderyCane(int age, boolean lit, boolean leave) {
        var state = MNDBlocks.POWDERY_CANE.get().defaultBlockState();
        if (state.hasProperty(PowderyCaneBlock.AGE)) {
            state = state.setValue(PowderyCaneBlock.AGE, age);
        }
        if (state.hasProperty(PowderyCaneBlock.LEAVE)) {
            state = state.setValue(PowderyCaneBlock.LEAVE, leave);
        }
        return makePowderyCropReady(state, lit);
    }

    private static BlockState bulletPepper(int age, boolean lit) {
        var state = MNDBlocks.BULLET_PEPPER.get().defaultBlockState();
        if (state.hasProperty(PowderyFlowerBlock.AGE)) {
            state = state.setValue(PowderyFlowerBlock.AGE, age);
        }
        return makePowderyCropReady(state, lit);
    }

    private static BlockState makePowderyCropReady(BlockState state, boolean lit) {
        state = makePowderyCropSafe(state);
        if (state.hasProperty(PowderyCaneBlock.LIT)) {
            state = state.setValue(PowderyCaneBlock.LIT, lit);
        }
        if (state.hasProperty(PowderyCannonBlock.LIT)) {
            state = state.setValue(PowderyCannonBlock.LIT, lit);
        }
        if (state.hasProperty(PowderyFlowerBlock.LIT)) {
            state = state.setValue(PowderyFlowerBlock.LIT, lit);
        }
        return state;
    }

    private static BlockState setLeaves(BlockState state, BambooLeaves leaves) {
        if (state.hasProperty(BlockStateProperties.BAMBOO_LEAVES)) {
            return state.setValue(BlockStateProperties.BAMBOO_LEAVES, leaves);
        }
        return state;
    }

    private static BlockState setStage(BlockState state, int stage) {
        if (state.hasProperty(BlockStateProperties.STAGE)) {
            return state.setValue(BlockStateProperties.STAGE, stage);
        }
        return state;
    }
}
