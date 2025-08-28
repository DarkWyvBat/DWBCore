package net.darkwyvbat.dwbcore.world.entity.specs;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.Set;

public interface PotionAttacker extends TacticalGearAgent {

    boolean hasAttackPotions();

    Set<Holder<MobEffect>> getAvailableAttackEffects();

    void preparePotionAttack(Holder<MobEffect> effect, EquipmentSlot slot);
}
