package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.darkwyvbat.dwbcore.world.entity.AbstractHumanoidEntity;
import net.darkwyvbat.dwbcore.world.entity.ai.perception.ActivityState;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class RandomWalkGoal extends Goal {
    public static final int DEFAULT_INTERVAL = 120;
    protected final AbstractHumanoidEntity mob;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    protected final double speedModifier;
    protected int interval;
    protected boolean forceTrigger;
    private final boolean checkNoActionTime;

    public RandomWalkGoal(AbstractHumanoidEntity pathfinderMob, double speed) {
        this(pathfinderMob, speed, DEFAULT_INTERVAL);
    }

    public RandomWalkGoal(AbstractHumanoidEntity pathfinderMob, double speed, int i) {
        this(pathfinderMob, speed, i, true);
    }

    public RandomWalkGoal(AbstractHumanoidEntity pathfinderMob, double speed, int i, boolean bl) {
        this.mob = pathfinderMob;
        this.speedModifier = speed;
        this.interval = i;
        this.checkNoActionTime = bl;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mob.getPerception().getProfile().getState().isGreater(ActivityState.REGULAR))
            return false;
        else {
            if (!this.forceTrigger) {
                if (this.checkNoActionTime && this.mob.getNoActionTime() >= 100) {
                    return false;
                }
                if (this.mob.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
                    return false;
                }
            }

            Vec3 vec3 = this.getPosition();
            if (vec3 == null)
                return false;
            else {
                this.wantedX = vec3.x;
                this.wantedY = vec3.y;
                this.wantedZ = vec3.z;
                this.forceTrigger = false;
                return true;
            }
        }
    }

    @Nullable
    protected Vec3 getPosition() {
        return LandRandomPos.getPos(this.mob, 10, 7);
    }


    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone() && mob.getPerception().getProfile().getState().isNotGreater(ActivityState.REGULAR);
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        super.stop();
    }
}