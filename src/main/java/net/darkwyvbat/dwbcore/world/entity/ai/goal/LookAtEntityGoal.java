package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.darkwyvbat.dwbcore.util.PoorRandom;
import net.darkwyvbat.dwbcore.world.entity.AbstractHumanoidEntity;
import net.darkwyvbat.dwbcore.world.entity.ai.perception.SensoryInput;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class LookAtEntityGoal extends Goal {
    protected final AbstractHumanoidEntity mob;
    protected SensoryInput sensor;
    protected Entity lookAt;
    protected final float lookDistance;
    private int lookTime;
    protected final float probability;
    private final boolean onlyHorizontal;
    protected final TargetingConditions lookAtContext;

    public LookAtEntityGoal(AbstractHumanoidEntity mob, float f) {
        this(mob, f, 0.02F);
    }

    public LookAtEntityGoal(AbstractHumanoidEntity mob, float f, float g) {
        this(mob, f, g, false);
    }

    public LookAtEntityGoal(AbstractHumanoidEntity mob, float f, float g, boolean bl) {
        this.mob = mob;
        this.lookDistance = f;
        this.probability = g;
        this.onlyHorizontal = bl;
        this.setFlags(EnumSet.of(Flag.LOOK));
        this.lookAtContext = TargetingConditions.forNonCombat().range(f);
    }

    @Override
    public boolean canUse() {
        sensor = mob.getPerception().getLastScan();
        if (PoorRandom.quickFloat() >= probability)
            return false;
        else {
            sensor.poiEntity().ifPresent(e -> lookAt = e);
            return lookAt != null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !(mob.distanceToSqr(lookAt) > lookDistance * lookDistance) && lookTime > 0;
    }

    @Override
    public void start() {
        lookTime = adjustedTickDelay(40 + mob.getRandom().nextInt(40));
    }

    @Override
    public void stop() {
        lookAt = null;
    }

    @Override
    public void tick() {
        double d = onlyHorizontal ? mob.getEyeY() : lookAt.getEyeY();
        mob.getLookControl().setLookAt(lookAt.getX(), d, lookAt.getZ());
        lookTime--;
    }
}
