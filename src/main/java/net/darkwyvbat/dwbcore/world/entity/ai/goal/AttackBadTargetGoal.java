package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.darkwyvbat.dwbcore.world.entity.PerceptionBasedMob;
import net.darkwyvbat.dwbcore.world.entity.ai.opinion.Reputation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class AttackBadTargetGoal extends TargetGoal {

    private final PerceptionBasedMob attacker;
    private final Reputation repThreshold;
    private final TargetingConditions attackConditions;

    @Nullable
    private LivingEntity target;

    public AttackBadTargetGoal(PerceptionBasedMob mob, Reputation repThreshold) {
        super(mob, true, false);
        this.attacker = mob;
        this.repThreshold = repThreshold;
        this.setFlags(EnumSet.of(Flag.TARGET));
        this.attackConditions = TargetingConditions.forCombat().range(this.getFollowDistance());
    }

    @Override
    public boolean canUse() {
        if (attacker.tickCount % 32 != 0)
            return false;
        this.findTarget();
        return this.target != null;
    }

    protected void findTarget() {
        LivingEntity closestTarget = null;
        double closestDistSqr = Double.MAX_VALUE;
        for (Entity entity : attacker.getPerception().getLastScan().entitiesAround()) {
            if (entity instanceof LivingEntity potentialTarget) {
                if (!(attacker.getPerception().getOpinions().getEntityRep(potentialTarget).isNotGreater(repThreshold) && canAttack(potentialTarget, attackConditions)))
                    continue;
                double distSqr = attacker.distanceToSqr(potentialTarget);
                if (distSqr < closestDistSqr) {
                    closestDistSqr = distSqr;
                    closestTarget = potentialTarget;
                }
            }
        }
        this.target = closestTarget;
    }

    @Override
    public void start() {
        this.attacker.setTarget(this.target);
        super.start();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

}