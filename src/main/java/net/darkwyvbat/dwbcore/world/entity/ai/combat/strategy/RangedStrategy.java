package net.darkwyvbat.dwbcore.world.entity.ai.combat.strategy;

import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatState;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStateView;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStrategy;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.WeaponCombatUsage;
import net.darkwyvbat.dwbcore.world.entity.ai.nav.MovementHelper;
import net.darkwyvbat.dwbcore.world.entity.specs.RangedAttacker;
import net.minecraft.world.InteractionHand;

public class RangedStrategy extends CombatStrategy {
    private final RangedAttacker rangedAttacker;

    public RangedStrategy(RangedAttacker rangedAttacker) {
        this.rangedAttacker = rangedAttacker;
    }

    @Override
    public void start(CombatState state, CombatStrategy prevStrategy) {
        if (!(prevStrategy instanceof KitingStrategy)) {
            state.getAttacker().stopUsingItem();
            state.startRangedCooldown(5);
        }
        if (!rangedAttacker.readyForRanged()) rangedAttacker.prepareRanged();
    }

    @Override
    public void tick(CombatState state) {
        if (state.isPathCooldownReady()) {
            if ((!state.canSeeTarget() && state.getSeeTime() < 0) || (state.canSeeTarget() && state.getDistanceSqr() > state.getConfig().rangedConfig().prefRangeSqr()))
                MovementHelper.tryPathToEntity(state.getAttacker(), state.getTarget());
            else if (state.canSeeTarget())
                state.getAttacker().getNavigation().stop();
            state.startPathCooldown(10);
        }
        WeaponCombatUsage.tryRanged(state, InteractionHand.MAIN_HAND);
    }

    @Override
    public void stop(CombatState state, CombatStrategy nextStrategy) {
        if (!(nextStrategy instanceof RangedStrategy)) state.getAttacker().stopUsingItem();
    }

    @Override
    public boolean canStart(CombatStateView state, CombatStrategy currentStrategy) {
        if (!rangedAttacker.hasRanged()) return false;
        return state.getDistanceSqr() > state.getConfig().rangedConfig().startDistSqr();
    }
}