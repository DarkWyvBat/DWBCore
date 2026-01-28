package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.darkwyvbat.dwbcore.util.PoorRandom;
import net.darkwyvbat.dwbcore.world.entity.PerceptionBasedMob;
import net.darkwyvbat.dwbcore.world.entity.ai.opinion.Reputation;
import net.darkwyvbat.dwbcore.world.entity.ai.perception.SensoryInput;
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
        if (PoorRandom.quickProb(0.05F))
            target = findTarget();
        return target != null;
    }

    protected LivingEntity findTarget() {
        LivingEntity closestTarget = null;
        double closestDistSqr = Double.MAX_VALUE;
        SensoryInput scan = attacker.getPerception().scanWorld();
        for (Entity entity : scan.entitiesAround()) {
            if (entity instanceof LivingEntity potentialTarget) {
                if (!(attacker.getOpinions().getEntityRep(potentialTarget).isNotGreater(repThreshold) && canAttack(potentialTarget, attackConditions)))
                    continue;
                double distSqr = attacker.distanceToSqr(potentialTarget);
                if (distSqr < closestDistSqr) {
                    closestDistSqr = distSqr;
                    closestTarget = potentialTarget;
                }
            }
        }
        return closestTarget;
    }

    @Override
    public void start() {
        attacker.setTarget(target);
        super.start();
    }
}