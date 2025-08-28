package net.darkwyvbat.dwbcore.world.entity.ai.combat.strategy;

import net.darkwyvbat.dwbcore.util.MathUtils;
import net.darkwyvbat.dwbcore.util.time.TimestampCooldown;
import net.darkwyvbat.dwbcore.world.entity.ai.AIUtils;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatState;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStateView;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStrategy;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.WeaponCombatUsage;
import net.darkwyvbat.dwbcore.world.entity.specs.PotionAttacker;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;

//TODO rethink
public class PotionAttackStrategy extends CombatStrategy {
    private final PotionAttacker potionAttacker;
    private Holder<MobEffect> effect;
    private final TimestampCooldown cd = new TimestampCooldown();

    public PotionAttackStrategy(PotionAttacker potionAttacker) {
        this.potionAttacker = potionAttacker;
    }

    @Override
    public void start(CombatState state, CombatStrategy prevStrategy) {
        potionAttacker.preparePotionAttack(effect, EquipmentSlot.OFFHAND);
        WeaponCombatUsage.tryRanged(state, InteractionHand.OFF_HAND);
        cd.set(40, state.timeNow());
    }

    @Override
    public boolean canStart(CombatStateView state, CombatStrategy currentStrategy) {
        if (cd.isReady(state.timeNow()) && !state.getAttacker().isUsingItem() && potionAttacker.hasAttackPotions() && MathUtils.isBetween(state.getDistanceSqr(), 25.0, 144.0)) {
            effect = AIUtils.getSuitableAttackPotion(state.getAttacker(), state.getTarget(), potionAttacker.getAvailableAttackEffects());
            return effect != null;
        }
        return false;
    }
}
