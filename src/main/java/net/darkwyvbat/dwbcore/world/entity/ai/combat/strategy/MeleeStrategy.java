package net.darkwyvbat.dwbcore.world.entity.ai.combat.strategy;

import net.darkwyvbat.dwbcore.util.PoorRandom;
import net.darkwyvbat.dwbcore.util.time.TickingCooldown;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatState;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStateView;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStrategy;
import net.darkwyvbat.dwbcore.world.entity.ai.nav.MovementHelper;
import net.darkwyvbat.dwbcore.world.entity.specs.AttackBlocker;
import net.darkwyvbat.dwbcore.world.entity.specs.MeleeAttacker;
import net.darkwyvbat.dwbcore.world.entity.specs.RangedAttacker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;

public class MeleeStrategy extends CombatStrategy {
    private final MeleeAttacker meleeAttacker;
    private final TickingCooldown shieldStateCD = new TickingCooldown();

    public MeleeStrategy(MeleeAttacker meleeAttacker) {
        this.meleeAttacker = meleeAttacker;
    }

    @Override
    public void start(CombatState state, CombatStrategy prevStrategy) {
        state.attacker().getNavigation().stop();
        meleeAttacker.prepareMelee();
        shieldStateCD.set(10);
    }

    @Override
    public void stop(CombatState state, CombatStrategy nextStrategy) {
        state.attacker().stopUsingItem();
    }

    @Override
    public void tick(CombatState state) {
        if (state.attacker() instanceof AttackBlocker attackBlocker) {
            if (shieldStateCD.tick()) {
                if (attackBlocker.readyForBlockAttack()) {
                    if (!state.attacker().isUsingItem() && (state.attacker().hurtTime != 0 || PoorRandom.quickProb(0.02F))) {
                        attackBlocker.startBlockAttack();
                        shieldStateCD.set(20);
                    } else if (PoorRandom.quickProb(0.01F) && state.attacker().hurtTime == 0) {
                        attackBlocker.stopBlockAttack();
                        shieldStateCD.set(10);
                    }
                } else
                    attackBlocker.prepareForAttackBlocking();
            }
        }

        if (state.isPathCooldownReady()) {
            if (!state.attacker().isWithinMeleeAttackRange(state.target()) || !state.canSeeTarget())
                MovementHelper.tryPathToEntity(state.attacker(), state.target(), state.config().meleeConfig().speed());
            else
                state.attacker().getNavigation().stop();
            state.startPathCooldown(pathToTargetCD(state.distanceSqr()));
        }
        if (state.isMeleeCooldownReady() && state.attacker().isWithinMeleeAttackRange(state.target()) && state.canSeeTarget()) {
            state.attacker().swing(InteractionHand.MAIN_HAND);
            state.attacker().doHurtTarget((ServerLevel) state.attacker().level(), state.target());
            state.startMeleeCooldown(state.config().meleeConfig().attackCD());
        }
    }

    @Override
    public boolean canStart(CombatStateView state, CombatStrategy currentStrategy) {
        if (state.attacker() instanceof RangedAttacker rangedAttacker)
            return !rangedAttacker.hasRanged() || state.distanceSqr() > state.config().rangedConfig().startDistSqr();

        return true;
    }

    public static int pathToTargetCD(double distSqr) {
        return distSqr > 64 ? 40 : 10;
    }
}
