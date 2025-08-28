package net.darkwyvbat.dwbcore.world.entity.ai.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public interface CombatStateView {
    LivingEntity getTarget();

    double getDistanceSqr();

    boolean canSeeTarget();

    int getSeeTime();

    Mob getAttacker();

    CombatConfig getConfig();

    long timeNow();

    CombatCooldowns getCooldowns();

    boolean isPathCooldownReady();

    boolean isMeleeCooldownReady();

    boolean isRangedCooldownReady();

}
