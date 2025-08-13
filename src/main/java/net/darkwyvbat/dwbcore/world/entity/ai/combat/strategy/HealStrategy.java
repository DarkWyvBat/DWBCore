package net.darkwyvbat.dwbcore.world.entity.ai.combat.strategy;

import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatState;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStrategy;
import net.darkwyvbat.dwbcore.world.entity.ai.nav.MovementHelper;
import net.minecraft.world.phys.Vec3;

public class HealStrategy extends CombatStrategy {

    @Override
    public void stop(CombatState state) {
        state.getMob().shouldConsumeNow = false;
    }

    @Override
    public void tick(CombatState state) {
        if (state.getDistanceSqr() < 49.0) {
            state.getMob().getNavigation().stop();
            Vec3 dir = MovementHelper.calcRetreat(state.getMob(),state.getTarget());
            if (MovementHelper.isSafeRetreat(state.getMob(), dir, 1.4)) {
                MovementHelper.doRetreat(state.getMob(), dir);
                state.setRetreating(true);
                state.getCooldowns().retreat().set(5);
            } else
                state.getMob().shouldConsumeNow = true;
        } else
            state.getMob().shouldConsumeNow = true;
    }

    @Override
    public boolean canStart(CombatState state) {
        return (state.getMob().getHealthPercent() < 0.5F || state.getPrevStrategy() instanceof HealStrategy) && state.getMob().setUpForHeal() && state.getMob().getHealthPercent() < 0.9;
    }
}
