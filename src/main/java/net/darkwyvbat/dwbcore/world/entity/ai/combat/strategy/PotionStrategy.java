package net.darkwyvbat.dwbcore.world.entity.ai.combat.strategy;

import net.darkwyvbat.dwbcore.util.MathUtils;
import net.darkwyvbat.dwbcore.util.time.Cooldown;
import net.darkwyvbat.dwbcore.world.entity.ai.AIUtils;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatState;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.CombatStrategy;
import net.darkwyvbat.dwbcore.world.entity.ai.combat.WeaponCombatUsage;
import net.darkwyvbat.dwbcore.world.entity.ai.goal.HumanoidCombatGoal;
import net.darkwyvbat.dwbcore.world.entity.inventory.InventoryItemCategory;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.Map;

//TODO rethink
public class PotionStrategy extends CombatStrategy {
    private Map<Holder<MobEffect>, Integer> availableEffects;
    private Holder<MobEffect> effect;
    private final Cooldown cd = new Cooldown();

    @Override
    public void start(CombatState state) {
        state.getMob().equipFromInventory(EquipmentSlot.OFFHAND, availableEffects.get(effect));
        WeaponCombatUsage.tryRanged(state, InteractionHand.OFF_HAND);
        cd.set(HumanoidCombatGoal.getConcertedTicks(40));
    }

    @Override
    public boolean canStart(CombatState state) {
        if (cd.tick() && !state.getMob().isUsingItem() && state.getMob().getInventoryManager().entryNotEmpty(InventoryItemCategory.ATTACK_POTION) && MathUtils.isBetween(state.getDistanceSqr(), 25.0, 144.0)) {
            availableEffects = state.getMob().getInventoryManager().getAvailablePotionEffectsWithIndices();
            effect = AIUtils.getSuitableAttackPotion(state.getMob(), state.getTarget(), availableEffects.keySet());
            return effect != null;
        }
        return false;
    }
}
