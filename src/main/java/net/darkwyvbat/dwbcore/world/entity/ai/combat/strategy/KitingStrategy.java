package net.darkwyvbat.dwbcore.world.entity.ai.combat.strategy;

import net.darkwyvbat.dwbcore.util.MathUtils;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatState;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStateView;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStrategy;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.WeaponCombatUsage;
import net.darkwyvbat.dwbcore.world.entity.ai.nav.MovementHelper;
import net.darkwyvbat.dwbcore.world.entity.specs.RangedAttacker;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class KitingStrategy extends CombatStrategy {
    private final RangedAttacker rangedAttacker;

    public KitingStrategy(RangedAttacker rangedAttacker) {
        this.rangedAttacker = rangedAttacker;
    }

    @Override
    public void start(CombatState state, CombatStrategy prevStrategy) {
        if (!rangedAttacker.readyForRanged()) rangedAttacker.prepareRanged();
        if (!(prevStrategy instanceof RangedStrategy))
            state.startRangedCooldown(5);
    }

    @Override
    public void stop(CombatState state, CombatStrategy nextStrategy) {
        if (!(nextStrategy instanceof RangedStrategy)) state.getAttacker().stopUsingItem();
    }

    @Override
    public void tick(CombatState state) {
        Mob mob = state.getAttacker();
        WeaponCombatUsage.tryRanged(state, InteractionHand.MAIN_HAND);
        if (state.isPathCooldownReady() && state.canSeeTarget()) {
            Vec3 dir = MovementHelper.calcRetreat(mob, state.getTarget());
            if (MovementHelper.isSafeRetreat(mob, dir, 1.2)) {
                MovementHelper.doRetreat(mob, dir);
            } else {
                if (state.isPathCooldownReady()) {
                    MovementHelper.tryPathAwayEntity((PathfinderMob) mob, mob.getTarget());
                    state.startPathCooldown(40);
                }
            }
        }
    }

    @Override
    public boolean canStart(CombatStateView state, CombatStrategy currentStrategy) {
        return rangedAttacker.hasRanged() && state.canSeeTarget() && MathUtils.isBetween(state.getDistanceSqr(), state.getConfig().rangedConfig().startDistSqr(), state.getConfig().rangedConfig().startKitingDistSqr());
    }
}
