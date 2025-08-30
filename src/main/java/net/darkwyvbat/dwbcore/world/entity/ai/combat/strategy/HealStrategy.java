package net.darkwyvbat.dwbcore.world.entity.ai.combat.strategy;

import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatState;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStateView;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStrategy;
import net.darkwyvbat.dwbcore.world.entity.ai.nav.MovementHelper;
import net.darkwyvbat.dwbcore.world.entity.specs.SelfCaring;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public class HealStrategy extends CombatStrategy {
    private final SelfCaring mob;

    public HealStrategy(SelfCaring mob) {
        this.mob = mob;
    }

    @Override
    public void start(CombatState state, CombatStrategy nextStrategy) {
        mob.prepareForCare(InteractionHand.OFF_HAND);
    }

    @Override
    public void stop(CombatState state, CombatStrategy nextStrategy) {
        mob.stopCaring();
    }

    @Override
    public void tick(CombatState state) {
        if (state.getDistanceSqr() < 49.0) {
            state.getAttacker().getNavigation().stop();
            Vec3 dir = MovementHelper.calcRetreat(state.getAttacker(), state.getTarget());
            if (MovementHelper.isSafeRetreat(state.getAttacker(), dir, 1.4)) {
                MovementHelper.doRetreat(state.getAttacker(), dir);
            } else
                mob.startCaring(InteractionHand.OFF_HAND);
        } else
            mob.startCaring(InteractionHand.OFF_HAND);
    }

    @Override
    public boolean canStart(CombatStateView state, CombatStrategy currentStrategy) {
        return (mob.getHealthPercent() < 0.5F || currentStrategy instanceof HealStrategy) && mob.getHealthPercent() < 0.9F && mob.hasForCare();
    }
}