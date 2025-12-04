package net.darkwyvbat.dwbcore.world.entity.inventory.preset;

import net.darkwyvbat.dwbcore.world.entity.inventory.CategoryCollector;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.EnumSet;
import java.util.Set;

public class InventoryCleanStrategies {
    public static final CategoryCollector FROM_LAST = (items, count, trash, inv, category) -> {
        for (int i = 0; i < count; i++) {
            if (items.size() - 1 - i < 0) break;
            trash.add(items.get(items.size() - 1 - i));
        }
    };
    public static final CategoryCollector ARMOR = (items, count, trash, inv, category) -> {
        Set<EquipmentSlot> set = EnumSet.noneOf(EquipmentSlot.class);
        int total = 0;
        for (int i : items) {
            if (total >= count) return;
            EquipmentSlot slot = inv.getItem(i).get(DataComponents.EQUIPPABLE).slot();
            if (!set.contains(slot)) set.add(slot);
            else {
                trash.add(i);
                ++total;
            }
        }
    };
}