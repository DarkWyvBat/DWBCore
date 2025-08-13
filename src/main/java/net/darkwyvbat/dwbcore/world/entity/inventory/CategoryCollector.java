package net.darkwyvbat.dwbcore.world.entity.inventory;

import net.minecraft.world.SimpleContainer;

import java.util.List;
import java.util.Set;

@FunctionalInterface
public interface CategoryCollector<C extends Enum<C>> {
    void collect(List<Integer> itemsInCategory, int slotsCount, Set<Integer> collectedIndices, SimpleContainer inventory, C category);
}