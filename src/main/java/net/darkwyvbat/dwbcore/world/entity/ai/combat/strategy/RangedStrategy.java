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
            state.attacker().stopUsingItem();
            state.startRangedCooldown(5);
        }
        if (!rangedAttacker.readyForRanged()) rangedAttacker.prepareRanged();
    }

    @Override
    public void tick(CombatState state) {
        if (state.isPathCdReady()) {
            if ((!state.canSeeTarget() && state.getSeeTime() < 0) || (state.canSeeTarget() && state.distanceSqr() > state.config().rangedConfig().prefRangeSqr()))
                MovementHelper.moveToEntity(state.attacker(), state.target(), 1.0);
            else if (state.canSeeTarget())
                state.attacker().getNavigation().stop();
            state.startPathCooldown(40);
        }
        WeaponCombatUsage.tryRanged(state, InteractionHand.MAIN_HAND);
    }

    @Override
    public void stop(CombatState state, CombatStrategy nextStrategy) {
        if (!(nextStrategy instanceof RangedStrategy)) state.attacker().stopUsingItem();
    }

    @Override
    public boolean canStart(CombatStateView state, CombatStrategy currentStrategy) {
        if (!rangedAttacker.hasRanged()) return false;
        return state.distanceSqr() > state.config().rangedConfig().startDistSqr();
    }
}