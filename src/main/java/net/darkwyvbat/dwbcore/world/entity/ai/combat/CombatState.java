package net.darkwyvbat.dwbcore.world.entity.ai.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class CombatState implements CombatStateView {
    private final Mob attacker;
    private final CombatConfig config;
    private final CombatCooldowns cooldowns = new CombatCooldowns();

    private LivingEntity target;
    private double distanceSqr;
    private boolean canSeeTarget;
    private int seeTime;

    public CombatState(Mob attacker, CombatConfig config) {
        this.attacker = attacker;
        this.config = config;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    public void setCanSeeTarget(boolean canSeeTarget) {
        this.canSeeTarget = canSeeTarget;
    }

    public void setDistanceSqr(double distanceSqr) {
        this.distanceSqr = distanceSqr;
    }

    public void setSeeTime(int seeTime) {
        this.seeTime = seeTime;
    }

    public void startPathCooldown(int v) {
        cooldowns.path().set(v, timeNow());
    }

    public void startMeleeCooldown(int v) {
        cooldowns.melee().set(v, timeNow());
    }

    public void startRangedCooldown(int v) {
        cooldowns.ranged().set(v, timeNow());
    }

    @Override
    public LivingEntity target() {
        return target;
    }

    @Override
    public double distanceSqr() {
        return distanceSqr;
    }

    @Override
    public boolean canSeeTarget() {
        return canSeeTarget;
    }

    @Override
    public int getSeeTime() {
        return seeTime;
    }

    @Override
    public Mob attacker() {
        return attacker;
    }

    @Override
    public CombatConfig config() {
        return config;
    }

    @Override
    public long timeNow() {
        return attacker.level().getGameTime();
    }

    @Override
    public boolean isPathCooldownReady() {
        return cooldowns.path().isReady(timeNow());
    }

    @Override
    public CombatCooldowns getCooldowns() {
        return cooldowns;
    }

    @Override
    public boolean isMeleeCooldownReady() {
        return cooldowns.melee().isReady(timeNow());
    }

    @Override
    public boolean isRangedCooldownReady() {
        return cooldowns.ranged().isReady(timeNow());
    }
}