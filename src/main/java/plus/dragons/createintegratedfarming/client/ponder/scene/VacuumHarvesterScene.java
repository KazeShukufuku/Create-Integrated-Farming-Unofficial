/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.client.ponder.scene;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import java.util.List;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import plus.dragons.createintegratedfarming.common.registry.CIFBlocks;

/** Compact 1.20 Ponder counterparts to the upstream vacuum-harvester scenes. */
public final class VacuumHarvesterScene {
    private VacuumHarvesterScene() {}

    public static void operate(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("vacuum_harvester.operate", "Charging and Harvesting Crops");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos machine = util.grid().at(2, 1, 2);
        BlockPos shaft = util.grid().at(2, 0, 2);
        BlockPos funnel = util.grid().at(1, 1, 2);
        List<BlockPos> crops = List.of(util.grid().at(1, 1, 1), util.grid().at(3, 1, 1),
                util.grid().at(1, 1, 3), util.grid().at(3, 1, 3));
        Selection cropSelection = positions(util, crops);
        scene.world().setBlocks(util.select().fromTo(1, 0, 1, 3, 0, 3), Blocks.FARMLAND.defaultBlockState(), false);
        for (BlockPos crop : crops)
            scene.world().setBlock(crop, mature(Blocks.WHEAT), false);
        scene.world().setBlock(machine, CIFBlocks.VACUUM_HARVESTER.getDefaultState(), false);
        scene.world().setBlock(shaft, AllBlocks.SHAFT.getDefaultState().setValue(BlockStateProperties.AXIS, Direction.Axis.Y), false);
        scene.world().showSection(util.select().fromTo(1, 0, 1, 3, 1, 3), Direction.UP);
        scene.world().setKineticSpeed(util.select().position(machine).add(util.select().position(shaft)), 64);
        scene.idle(20);

        scene.overlay().showText(80).text("Rotational power charges the vacuum chamber")
                .pointAt(util.vector().topOf(machine)).placeNearTarget();
        scene.idle(90);
        scene.overlay().showOutline(PonderPalette.OUTPUT, cropSelection, cropSelection, 50);
        for (BlockPos crop : crops)
            scene.world().modifyBlock(crop, state -> state.setValue(CropBlock.AGE, 0), true);
        scene.effects().indicateSuccess(machine);
        scene.overlay().showText(70).attachKeyFrame()
                .text("When fully charged, it harvests mature crops in its configured area")
                .pointAt(util.vector().centerOf(machine)).placeNearTarget();
        scene.idle(80);

        scene.world().setBlock(funnel, AllBlocks.BRASS_FUNNEL.getDefaultState()
                .setValue(FunnelBlock.FACING, Direction.WEST).setValue(FunnelBlock.EXTRACTING, true), false);
        scene.world().showSection(util.select().position(funnel), Direction.EAST);
        scene.overlay().showText(70).text("Harvests are stored internally and can be extracted from every side except below")
                .pointAt(util.vector().centerOf(funnel)).placeNearTarget();
        scene.idle(80);
    }

    public static void contraption(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("vacuum_harvester.contraption", "Using a Vacuum Harvester on Contraptions");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.scaleSceneView(.9F);

        BlockPos bearing = util.grid().at(4, 1, 4);
        BlockPos machine = util.grid().at(1, 2, 4);
        BlockPos controls = util.grid().at(3, 2, 4);
        Selection assemblySelection = util.select().fromTo(1, 1, 4, 3, 2, 4);
        scene.world().setBlock(bearing, AllBlocks.MECHANICAL_BEARING.getDefaultState()
                .setValue(BlockStateProperties.FACING, Direction.UP), false);
        scene.world().setBlocks(util.select().fromTo(1, 1, 4, 3, 1, 4),
                AllBlocks.LINEAR_CHASSIS.getDefaultState().setValue(BlockStateProperties.AXIS, Direction.Axis.X), false);
        scene.world().setBlock(machine, CIFBlocks.VACUUM_HARVESTER.getDefaultState(), false);
        scene.world().setBlock(controls, AllBlocks.CONTRAPTION_CONTROLS.getDefaultState(), false);
        scene.world().setFilterData(util.select().position(controls), ContraptionControlsBlockEntity.class, CIFBlocks.VACUUM_HARVESTER.asStack());
        scene.world().showSection(util.select().position(bearing), Direction.DOWN);
        ElementLink<WorldSectionElement> assembly = scene.world().showIndependentSection(assemblySelection, Direction.DOWN);
        scene.world().configureCenterOfRotation(assembly, util.vector().centerOf(bearing));
        scene.idle(20);

        scene.overlay().showText(70).text("On an assembled Contraption, the harvester charges on its own timer")
                .independent().placeNearTarget();
        scene.world().setKineticSpeed(util.select().position(bearing), -32);
        scene.world().rotateBearing(bearing, -90, 50);
        scene.world().rotateSection(assembly, 0, -90, 0, 50);
        scene.idle(70);
        scene.overlay().showText(75).attachKeyFrame()
                .text("Each pulse harvests the complete area around its current position")
                .independent().placeNearTarget();
        scene.idle(80);
        scene.overlay().showText(70).attachKeyFrame()
                .text("Contraption Controls pause charging until this actor is enabled again")
                .pointAt(util.vector().centerOf(controls)).placeNearTarget();
        scene.idle(70);
    }

    private static net.minecraft.world.level.block.state.BlockState mature(net.minecraft.world.level.block.Block block) {
        return block.defaultBlockState().setValue(CropBlock.AGE, 7);
    }

    private static Selection positions(SceneBuildingUtil util, List<BlockPos> positions) {
        Selection selection = util.select().position(positions.get(0));
        for (int i = 1; i < positions.size(); i++)
            selection = selection.add(util.select().position(positions.get(i)));
        return selection;
    }
}
