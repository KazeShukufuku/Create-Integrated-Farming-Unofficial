package plus.dragons.createintegratedfarming.common.farming.vacuum;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import plus.dragons.createintegratedfarming.client.renderer.VacuumHarvesterRenderer;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.api.harvester.CustomHarvestBehaviour;
import plus.dragons.createintegratedfarming.config.CIFConfig;

/** Runs the same area-harvesting cycle when the machine is carried by a contraption. */
public class VacuumHarvesterMovementBehaviour implements MovementBehaviour {
    private static final String HEAD_OFFSET = "VacuumHeadOffset";
    private static final String PREVIOUS_HEAD_OFFSET = "VacuumPreviousHeadOffset";

    @Override
    public void tick(MovementContext context) {
        if (context.position == null)
            return;
        context.data.putFloat(PREVIOUS_HEAD_OFFSET, context.data.getFloat(HEAD_OFFSET));
        double progress = context.data.getDouble(VacuumHarvesterCycle.CHARGE_PROGRESS);
        int releaseTicks = context.data.getInt(VacuumHarvesterCycle.RELEASE_TICKS);
        if (releaseTicks > 0) {
            if (--releaseTicks == 0)
                progress = 0;
        } else {
            progress = VacuumHarvesterCycle.advanceCharge(progress, VacuumHarvesterCycle.contraptionIncrement());
            if (progress >= 1) {
                releaseTicks = VacuumHarvesterCycle.RELEASE_DURATION;
                if (!context.world.isClientSide)
                    harvestArea(context);
            }
        }
        float headOffset = VacuumHarvesterCycle.getHeadOffset(progress, releaseTicks);
        context.data.putDouble(VacuumHarvesterCycle.CHARGE_PROGRESS, progress);
        context.data.putInt(VacuumHarvesterCycle.RELEASE_TICKS, releaseTicks);
        context.data.putFloat(HEAD_OFFSET, headOffset);
        if (context.world.isClientSide
                && releaseTicks == 0
                && Math.floorMod(context.world.getGameTime() + context.localPos.hashCode(), 8) == 0)
            VacuumHarvesterEffects.spawnExhaust(
                    context.world, VacuumHarvesterEffects.intake(context.position, headOffset));
    }

    private void harvestArea(MovementContext context) {
        var storage = context.contraption.getStorage().getAllItems();
        AreaHarvestContext harvestContext = new AreaHarvestContext(
                context.world, true, false, CustomHarvestBehaviour.getHarvestTool(context),
                stack -> dropItem(context, stack),
                (predicate, amount) -> ItemHelper.extract(storage, predicate, amount, false));
        var result = VacuumHarvesterHarvesting.harvestArea(
                harvestContext, BlockPos.containing(context.position), CIFConfig.server().vacuumHarvesterRange.get());
        VacuumHarvesterEffects.emitSuction(
                (ServerLevel) context.world,
                VacuumHarvesterEffects.intake(context.position, VacuumHarvesterCycle.MAX_HEAD_OFFSET),
                result.particleSources());
    }

    public static float getRenderedHeadOffset(MovementContext context) {
        return context.data.getFloat(HEAD_OFFSET);
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
            ContraptionMatrices matrices, MultiBufferSource buffers) {
        VacuumHarvesterRenderer.renderInContraption(context, renderWorld, matrices, buffers);
    }
}
