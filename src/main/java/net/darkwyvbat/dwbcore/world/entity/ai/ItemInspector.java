package net.darkwyvbat.dwbcore.world.entity.ai;

import net.darkwyvbat.dwbcore.world.entity.inventory.HumanoidInventoryManager;
import net.darkwyvbat.dwbcore.world.entity.inventory.ItemCategory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface ItemInspector {
    boolean isWanted(Mob mob, ItemStack item, ItemCategory category, HumanoidInventoryManager inventoryManager);
}