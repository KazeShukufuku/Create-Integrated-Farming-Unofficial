package plus.dragons.createintegratedfarming.common.farming.vacuum;

import net.minecraft.util.Mth;
import plus.dragons.createintegratedfarming.config.CIFConfig;

/** Shared timing rules for stationary and contraption-mounted harvesters. */
public final class VacuumHarvesterCycle {
    public static final String CHARGE_PROGRESS = "VacuumChargeProgress";
    public static final String RELEASE_TICKS = "VacuumReleaseTicks";
    public static final int RELEASE_DURATION = 5;
    public static final float MAX_HEAD_OFFSET = 6.0F / 16.0F;
    private VacuumHarvesterCycle() {}

    public static double stationaryIncrement(float speed) {
        if (speed == 0)
            return 0;
        double ticks = Math.max(20, Math.ceil(CIFConfig.server().vacuumHarvesterChargeTime.get() * 64D / Math.abs(speed)));
        return 1D / ticks;
    }

    public static double contraptionIncrement() {
        return 1D / CIFConfig.server().vacuumHarvesterChargeTime.get();
    }

    public static double advanceCharge(double chargeProgress, double increment) {
        double next = Mth.clamp(chargeProgress + increment, 0, 1);
        return next >= 1 - 1.0E-9 ? 1 : next;
    }

    public static float getHeadOffset(double chargeProgress, int releaseTicks) {
        if (releaseTicks > 0) {
            float remaining = Mth.clamp(releaseTicks / (float) RELEASE_DURATION, 0, 1);
            return MAX_HEAD_OFFSET * remaining * remaining;
        }
        float progress = (float) Mth.clamp(chargeProgress, 0, 1);
        float eased = progress * progress * (3 - 2 * progress);
        return MAX_HEAD_OFFSET * eased;
    }
}
