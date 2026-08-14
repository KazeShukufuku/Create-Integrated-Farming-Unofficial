/*
 * Copyright (C) 2025 DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import plus.dragons.createintegratedfarming.client.CIFPartialModels;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterBlockEntity;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterMovementBehaviour;

/** Renders the vacuum harvester's telescoping upper assembly. */
public class VacuumHarvesterRenderer extends SafeBlockEntityRenderer<VacuumHarvesterBlockEntity> {
    public VacuumHarvesterRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(VacuumHarvesterBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
            MultiBufferSource bufferSource, int light, int overlay) {
        SuperByteBuffer moving = CachedBuffers.partial(
                CIFPartialModels.VACUUM_HARVESTER_MOVING, blockEntity.getBlockState());
        moving.translate(0, -blockEntity.getRenderedHeadOffset(partialTicks), 0)
                .light(light)
                .renderInto(poseStack, bufferSource.getBuffer(RenderType.cutoutMipped()));
    }

    public static void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
            ContraptionMatrices matrices, MultiBufferSource bufferSource) {
        SuperByteBuffer moving = CachedBuffers.partial(CIFPartialModels.VACUUM_HARVESTER_MOVING, context.state);
        moving.transform(matrices.getModel())
                .translate(0, -VacuumHarvesterMovementBehaviour.getRenderedHeadOffset(context), 0)
                .light(LevelRenderer.getLightColor(renderWorld, context.localPos))
                .useLevelLight(context.world, matrices.getWorld())
                .renderInto(matrices.getViewProjection(), bufferSource.getBuffer(RenderType.cutoutMipped()));
    }
}
