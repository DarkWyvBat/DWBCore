package net.darkwyvbat.dwbcore.world.entity.ai.combat.strategy;

import net.darkwyvbat.dwbcore.util.MathUtils;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatState;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStrategy;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.WeaponCombatUsage;
import net.darkwyvbat.dwbcore.world.entity.ai.nav.MovementHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public class KitingStrategy extends CombatStrategy {
    @Override
    public void start(CombatState state) {
        if (!state.getMob().holdRangedWeapon()) state.getMob().setUpRangedWeapon();
        if (!(state.getPrevStrategy() instanceof RangedStrategy))
            state.startRangedCooldown(5);
    }

    @Override
    public void stop(CombatState state) {
        if (!(state.getNextStrategy() instanceof RangedStrategy)) state.getMob().stopUsingItem();
        state.setRetreating(false);
    }

    @Override
    public void tick(CombatState state) {
        WeaponCombatUsage.tryRanged(state, InteractionHand.MAIN_HAND);
        if (state.isPathCooldownReady() && state.canSeeTarget()) {
            Vec3 dir = MovementHelper.calcRetreat(state.getMob(), state.getTarget());
            if (MovementHelper.isSafeRetreat(state.getMob(), dir, 1.2)) {
                MovementHelper.doRetreat(state.getMob(), dir);
                state.setRetreating(true);
            } else {
                if (state.isPathCooldownReady()) {
                    MovementHelper.tryPathAwayTarget(state.getMob());
                    state.startPathCooldown(40);
                }
            }
        }
    }

    @Override
    public boolean canStart(CombatState state) {
        return state.getMob().hasRangedWeapon() && state.canSeeTarget() && MathUtils.isBetween(state.getDistanceSqr(), state.getConfig().meleeConfig().maxDistSqr(), state.getConfig().rangedConfig().startKitingDistSqr());
    }
}
