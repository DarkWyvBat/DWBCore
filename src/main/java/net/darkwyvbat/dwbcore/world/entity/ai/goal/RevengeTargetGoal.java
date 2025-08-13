package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class RevengeTargetGoal extends TargetGoal {

    private static final TargetingConditions REVENGE_CONDITIONS = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    private final Predicate<LivingEntity> predicate;
    @Nullable
    private LivingEntity revengeTarget;
    private int lastHurtByMobTimestamp;

    public RevengeTargetGoal(PathfinderMob mob, Predicate<LivingEntity> predicate) {
        super(mob, false, false);
        this.predicate = predicate;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        int currentTimestamp = this.mob.getLastHurtByMobTimestamp();
        LivingEntity lastAttacker = this.mob.getLastHurtByMob();
        if (lastAttacker == null || currentTimestamp == this.lastHurtByMobTimestamp)
            return false;
        if (!this.predicate.test(lastAttacker))
            return false;
        if (!this.canAttack(lastAttacker, REVENGE_CONDITIONS))
            return false;

        this.revengeTarget = lastAttacker;
        return true;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.revengeTarget);
        this.targetMob = this.revengeTarget;
        this.lastHurtByMobTimestamp = this.mob.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 400;
        super.start();
    }
}