package net.darkwyvbat.dwbcore.world.entity.ai.combat.strategy;

import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatState;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStrategy;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.WeaponCombatUsage;
import net.darkwyvbat.dwbcore.world.entity.ai.nav.MovementHelper;
import net.minecraft.world.InteractionHand;

public class RangedStrategy extends CombatStrategy {

    @Override
    public void start(CombatState state) {
        if (!(state.getPrevStrategy() instanceof KitingStrategy)) {
            state.getMob().stopUsingItem();
            state.getCooldowns().ranged().set(5);
        }
        if (!state.getMob().holdRangedWeapon()) state.getMob().setUpRangedWeapon();
    }

    @Override
    public void tick(CombatState state) {
        if (state.isPathCooldownReady()) {
            if ((!state.canSeeTarget() && state.getSeeTime() < 0) || (state.canSeeTarget() && state.getDistanceSqr() > state.getConfig().rangedConfig().prefRangeSqr()))
                MovementHelper.tryPathToEntity(state.getMob(), state.getMob().getTarget());
            else if (state.canSeeTarget())
                state.getMob().getNavigation().stop();
            state.startPathCooldown(10);
        }
        WeaponCombatUsage.tryRanged(state, InteractionHand.MAIN_HAND);
    }

    @Override
    public void stop(CombatState state) {
        if (!(state.getNextStrategy() instanceof RangedStrategy)) state.getMob().stopUsingItem();
    }

    @Override
    public boolean canStart(CombatState state) {
        if (!state.getMob().hasRangedWeapon()) return false;
        return state.getDistanceSqr() > 25;
    }
}