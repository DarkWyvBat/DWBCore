package net.darkwyvbat.dwbcore.world.entity.inventory;

import net.minecraft.world.item.ItemStack;

import java.util.Set;

public interface ItemCategorizer<C extends Enum<C>> {
    Set<C> categorize(ItemStack item);

    Class<C> getCategoryClass();
}