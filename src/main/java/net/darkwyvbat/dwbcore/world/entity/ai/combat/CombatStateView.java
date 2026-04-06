package net.darkwyvbat.dwbcore.world.entity.ai.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public interface CombatStateView {
    LivingEntity target();

    double distanceSqr();

    boolean canSeeTarget();

    int getSeeTime();

    Mob attacker();

    CombatConfig config();

    long timeNow();

    CombatCooldowns cooldowns();

    boolean isPathCdReady();

    boolean isMeleeCooldownReady();

    boolean isRangedCooldownReady();

}
