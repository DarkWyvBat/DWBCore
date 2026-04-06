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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class MeleeStrategy extends CombatStrategy {
    private final MeleeAttacker meleeAttacker;
    private final TickingCooldown shieldStateCD = new TickingCooldown();

    public MeleeStrategy(MeleeAttacker meleeAttacker) {
        this.meleeAttacker = meleeAttacker;
    }

    @Override
    public void start(CombatState state, CombatStrategy prevStrategy) {
        state.startPathCooldown(0);
        meleeAttacker.prepareMelee();
        shieldStateCD.set(10);
    }

    @Override
    public void stop(CombatState state, CombatStrategy nextStrategy) {
        state.attacker().stopUsingItem();
    }

    @Override
    public void tick(CombatState state) {
        LivingEntity target = state.target();
        Mob attacker = state.attacker();
        if (attacker instanceof AttackBlocker attackBlocker) {
            if (shieldStateCD.tick()) {
                if (attackBlocker.readyForBlockAttack()) {
                    if (!attacker.isUsingItem() && (attacker.hurtTime != 0 || PoorRandom.quickProb(0.02F))) {
                        attackBlocker.startBlockAttack();
                        shieldStateCD.set(20);
                    } else if (PoorRandom.quickProb(0.01F) && attacker.hurtTime == 0) {
                        attackBlocker.stopBlockAttack();
                        shieldStateCD.set(10);
                    }
                } else
                    attackBlocker.prepareForAttackBlocking();
            }
        }
        if (state.isPathCdReady()) {
            if (!attacker.isWithinMeleeAttackRange(target) || !state.canSeeTarget())
                MovementHelper.tryPathToTargetCd(state);
        }
        if (state.isMeleeCooldownReady() && attacker.isWithinMeleeAttackRange(target) && state.canSeeTarget()) {
            state.attacker().swing(InteractionHand.MAIN_HAND);
            state.attacker().doHurtTarget((ServerLevel) state.attacker().level(), state.target());
            state.startMeleeCooldown(state.config().meleeConfig().attackCD());
        }
        if (attacker.tickCount % 8 == 0 && state.attacker().onGround()) {
            double dX = target.getX() - attacker.getX(), dY = target.getY() - attacker.getY(), dZ = target.getZ() - attacker.getZ();
            if (dX * dX + dZ * dZ < 2.5 && dY > 1.0 && dY < 3.5) attacker.getJumpControl().jump();
        }
    }

    @Override
    public boolean canStart(CombatStateView state, CombatStrategy currentStrategy) {
        if (state.attacker() instanceof RangedAttacker rangedAttacker)
            return !rangedAttacker.hasRanged() || state.distanceSqr() > state.config().rangedConfig().startDistSqr();

        return true;
    }
}