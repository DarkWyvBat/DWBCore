package net.darkwyvbat.dwbcore.world.entity.ai.combat;

import net.darkwyvbat.dwbcore.world.entity.AbstractInventoryHumanoid;
import net.minecraft.world.entity.LivingEntity;

public class CombatState {
    private final AbstractInventoryHumanoid mob;
    private final CombatConfig config;
    private final CombatCooldowns cooldowns;

    private LivingEntity target;
    private double distanceSqr;
    private boolean canSeeTarget;
    private int seeTime;
    private boolean retreating;
    private CombatStrategy prevStrategy;
    private CombatStrategy nextStrategy;

    public CombatState(AbstractInventoryHumanoid mob, CombatConfig config, CombatCooldowns cooldowns) {
        this.mob = mob;
        this.config = config;
        this.cooldowns = cooldowns;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    public double getDistanceSqr() {
        return distanceSqr;
    }

    public void setDistanceSqr(double distanceSqr) {
        this.distanceSqr = distanceSqr;
    }

    public boolean canSeeTarget() {
        return canSeeTarget;
    }

    public void setCanSeeTarget(boolean canSeeTarget) {
        this.canSeeTarget = canSeeTarget;
    }

    public int getSeeTime() {
        return seeTime;
    }

    public void setSeeTime(int seeTime) {
        this.seeTime = seeTime;
    }

    public boolean isRetreating() {
        return retreating;
    }

    public void setRetreating(boolean retreating) {
        this.retreating = retreating;
    }

    public AbstractInventoryHumanoid getMob() {
        return mob;
    }

    public CombatConfig getConfig() {
        return config;
    }

    public CombatCooldowns getCooldowns() {
        return cooldowns;
    }

    public boolean isPathCooldownReady() {
        return cooldowns.path().isReady();
    }

    public boolean isMeleeCooldownReady() {
        return cooldowns.melee().isReady();
    }

    public boolean isRangedCooldownReady() {
        return cooldowns.ranged().isReady();
    }

    public void startPathCooldown(int v) {
        cooldowns.path().set(v);
    }

    public void startMeleeCooldown(int v) {
        cooldowns.melee().set(v);
    }

    public void startRangedCooldown(int v) {
        cooldowns.ranged().set(v);
    }

    public CombatStrategy getPrevStrategy() {
        return prevStrategy;
    }

    public void setPrevStrategy(CombatStrategy prevStrategy) {
        this.prevStrategy = prevStrategy;
    }

    public CombatStrategy getNextStrategy() {
        return nextStrategy;
    }

    public void setNextStrategy(CombatStrategy nextStrategy) {
        this.nextStrategy = nextStrategy;
    }
}