package net.darkwyvbat.dwbcore.world.entity.ai.combat.strategy;

import net.darkwyvbat.dwbcore.world.entity.AbstractInventoryHumanoid;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatState;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStateView;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStrategy;
import net.darkwyvbat.dwbcore.world.entity.ai.nav.MovementHelper;
import net.minecraft.world.phys.Vec3;

public class HealStrategy extends CombatStrategy {
    private final AbstractInventoryHumanoid mob;

    public HealStrategy(AbstractInventoryHumanoid mob) {
        this.mob = mob;
    }

    @Override
    public void stop(CombatState state, CombatStrategy nextStrategy) {
        mob.shouldConsumeNow = false;
    }

    @Override
    public void tick(CombatState state) {
        if (state.getDistanceSqr() < 49.0) {
            mob.getNavigation().stop();
            Vec3 dir = MovementHelper.calcRetreat(mob, state.getTarget());
            if (MovementHelper.isSafeRetreat(mob, dir, 1.4)) {
                MovementHelper.doRetreat(mob, dir);
            } else
                mob.shouldConsumeNow = true;
        } else
            mob.shouldConsumeNow = true;
    }

    @Override
    public boolean canStart(CombatStateView state, CombatStrategy currentStrategy) {
        return (mob.getHealthPercent() < 0.5F || currentStrategy instanceof HealStrategy) && mob.getHealthPercent() < 0.9F && mob.setUpForHeal();
    }
}
