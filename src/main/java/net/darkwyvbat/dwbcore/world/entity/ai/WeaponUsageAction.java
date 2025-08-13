package net.darkwyvbat.dwbcore.world.entity.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface WeaponUsageAction {
    void use(LivingEntity user, ItemStack weapon, Entity target, float charge);
}
