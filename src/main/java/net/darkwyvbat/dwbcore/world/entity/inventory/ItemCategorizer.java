package net.darkwyvbat.dwbcore.world.entity.inventory;

import net.minecraft.world.item.ItemStack;

import java.util.Set;

public interface ItemCategorizer {
    Set<ItemCategory> categorize(ItemStack item);
}