package net.darkwyvbat.dwbcore.world.entity.ai.combat.strategy;

import net.darkwyvbat.dwbcore.util.time.TickingCooldown;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatState;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStateView;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStrategy;
import net.darkwyvbat.dwbcore.world.entity.ai.nav.MovementHelper;
import net.darkwyvbat.dwbcore.world.entity.specs.SelfCaring;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class HealStrategy extends CombatStrategy {
    private final SelfCaring mob;
    private final TickingCooldown healCd = new TickingCooldown();

    public HealStrategy(SelfCaring mob) {
        this.mob = mob;
    }

    @Override
    public void stop(CombatState state, CombatStrategy nextStrategy) {
        mob.stopCaring();
    }

    @Override
    public void tick(CombatState state) {
        healCd.tick();
        if (state.distanceSqr() < 49.0) {
            state.attacker().getNavigation().stop();
            Vec3 dir = MovementHelper.calcRetreat(state.attacker(), state.target());
            if (MovementHelper.isSafeRetreat(state.attacker(), dir, 1.1)) {
                MovementHelper.doRetreat(state.attacker(), dir, 0.15);
            } else
                tryStartCaring();
        } else
            tryStartCaring();
    }

    @Override
    public boolean canStart(CombatStateView state, CombatStrategy currentStrategy) {
        return (mob.getHealthPercent() < 0.5F || currentStrategy instanceof HealStrategy) && mob.getHealthPercent() < 0.9F && mob.hasForCare();
    }

    private void tryStartCaring() {
        if (healCd.isReady()) {
            LivingEntity livingEntity = (LivingEntity) mob;
            livingEntity.stopUsingItem();
            mob.prepareForCare(InteractionHand.OFF_HAND);
            mob.startCaring(InteractionHand.OFF_HAND);
            healCd.set(livingEntity.getItemInHand(InteractionHand.OFF_HAND).getUseDuration(livingEntity) + 5);
        }
    }
}