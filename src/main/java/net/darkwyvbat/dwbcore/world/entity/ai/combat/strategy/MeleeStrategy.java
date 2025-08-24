package net.darkwyvbat.dwbcore.world.entity.ai.combat.strategy;

import net.darkwyvbat.dwbcore.util.PoorRandom;
import net.darkwyvbat.dwbcore.util.time.Cooldown;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatState;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStrategy;
import net.darkwyvbat.dwbcore.world.entity.ai.nav.MovementHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;

public class MeleeStrategy extends CombatStrategy {
    private final Cooldown shieldStateCD = new Cooldown();

    @Override
    public void start(CombatState state) {
        state.getMob().getNavigation().stop();
        state.getMob().setUpMeleeWeapon();
        shieldStateCD.set(10, true);
    }

    @Override
    public void stop(CombatState state) {
        state.getMob().stopUsingItem();
    }

    @Override
    public void tick(CombatState state) {
        if (shieldStateCD.tick()) {
            if (state.getMob().getItemInHand(InteractionHand.OFF_HAND).getItem() == Items.SHIELD) {
                if (!state.getMob().isUsingItem() && (state.getMob().hurtTime != 0 || PoorRandom.quickProb(0.02F)) && state.getMob().getUseItemCD().isReady()) {
                    state.getMob().startUsingItem(InteractionHand.OFF_HAND);
                    shieldStateCD.set(20);
                } else if (PoorRandom.quickProb(0.01F) && state.getMob().hurtTime == 0) {
                    state.getMob().stopUsingItem();
                    shieldStateCD.set(10);
                }
            } else
                state.getMob().setUpShield();
        }

        if (state.isPathCooldownReady()) {
            if (!state.getMob().isWithinMeleeAttackRange(state.getTarget()) || !state.canSeeTarget())
                MovementHelper.tryPathToEntity(state.getMob(), state.getMob().getTarget(), state.getConfig().meleeConfig().speed());
            else
                state.getMob().getNavigation().stop();
            state.startPathCooldown(pathToTargetCD(state.getDistanceSqr()));
        }
        if (state.isMeleeCooldownReady() && state.getMob().isWithinMeleeAttackRange(state.getTarget()) && state.canSeeTarget()) {
            state.getMob().swing(InteractionHand.MAIN_HAND);
            state.getMob().doHurtTarget((ServerLevel) state.getMob().level(), state.getTarget());
            state.startMeleeCooldown(state.getConfig().meleeConfig().cd());
        }
    }

    @Override
    public boolean canStart(CombatState state) {
        return !state.getMob().hasRangedWeapon() || state.getDistanceSqr() < state.getConfig().meleeConfig().maxDistSqr();
    }

    public static int pathToTargetCD(double distSqr) {
        return distSqr > 64 ? 40 : 10;
    }
}
