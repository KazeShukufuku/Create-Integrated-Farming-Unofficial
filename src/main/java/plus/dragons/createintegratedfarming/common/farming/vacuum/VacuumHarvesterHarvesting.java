package plus.dragons.createintegratedfarming.common.farming.vacuum;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.common.farming.harvest.StandardAreaHarvests;

public final class VacuumHarvesterHarvesting {
    private static final int MAX_PARTICLE_SOURCES = 12;

    private VacuumHarvesterHarvesting() {}

    public static HarvestResult harvestArea(AreaHarvestContext context, BlockPos center, int range) {
        int harvested = 0;
        List<BlockPos> particleSources = new ArrayList<>(MAX_PARTICLE_SOURCES);
        for (int y = center.getY() - 1; y <= center.getY() + 1; y++)
            for (int x = center.getX() - range; x <= center.getX() + range; x++)
                for (int z = center.getZ() - range; z <= center.getZ() + range; z++) {
                    BlockPos target = new BlockPos(x, y, z);
                    if (context.level().isLoaded(target)
                            && StandardAreaHarvests.harvest(context, target, context.level().getBlockState(target))) {
                        harvested++;
                        if (particleSources.size() < MAX_PARTICLE_SOURCES) {
                            particleSources.add(target);
                        } else {
                            int replacement = context.level().random.nextInt(harvested);
                            if (replacement < MAX_PARTICLE_SOURCES)
                                particleSources.set(replacement, target);
                        }
                    }
                }
        return new HarvestResult(harvested, List.copyOf(particleSources));
    }

    public record HarvestResult(int harvested, List<BlockPos> particleSources) {}
}
