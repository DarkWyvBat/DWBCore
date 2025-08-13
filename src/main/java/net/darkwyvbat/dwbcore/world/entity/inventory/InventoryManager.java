package net.darkwyvbat.dwbcore.world.entity.inventory;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class InventoryManager<C extends Enum<C>> {

    public static final int INVALID_INDEX = -1;

    private final SimpleContainer inventory;
    private final ItemCategorizer<C> categorizer;
    private final Map<C, Comparator<ItemStack>> itemComparators;
    private final List<C> importanceOrder;
    protected Map<C, List<Integer>> inventoryEntries;

    protected final Map<EquipmentSlot, Integer> equipmentSlotsInvRefs;

    public InventoryManager(SimpleContainer sourceInventory, ItemCategorizer<C> categorizer, Map<C, Comparator<ItemStack>> itemComparators, List<C> importanceOrder) {
        this.inventory = sourceInventory;
        this.categorizer = categorizer;
        this.itemComparators = itemComparators;
        this.importanceOrder = importanceOrder;
        this.inventoryEntries = new EnumMap<>(categorizer.getCategoryClass());
        for (C categoryValue : categorizer.getCategoryClass().getEnumConstants())
            this.inventoryEntries.put(categoryValue, new ArrayList<>());

        equipmentSlotsInvRefs = new EnumMap<>(EquipmentSlot.class);
        for (EquipmentSlot slot : EquipmentSlot.values())
            equipmentSlotsInvRefs.put(slot, null);
    }

    public Set<Integer> getPotentialTrash(int slotsCount, Map<C, CategoryCollector<C>> categoryStrategies) {
        Set<Integer> trash = new HashSet<>();
        for (C category : this.importanceOrder) {
            if (trash.size() >= slotsCount) break;

            List<Integer> itemsInCategory = inventoryEntries.get(category);
            if (itemsInCategory == null || itemsInCategory.isEmpty()) continue;
            int needFromThisCategory = slotsCount - trash.size();
            if (needFromThisCategory <= 0) break;
            int currentCategorySize = itemsInCategory.size();
            int slotsToFree = Math.min(needFromThisCategory, currentCategorySize);
            CategoryCollector<C> strategy = categoryStrategies != null ? categoryStrategies.get(category) : null;
            if (strategy != null)
                strategy.collect(itemsInCategory, slotsToFree, trash, this.inventory, category);
            else {
                for (int i = 0; i < slotsToFree; ++i) {
                    if (currentCategorySize - 1 - i < 0) break;
                    trash.add(itemsInCategory.get(currentCategorySize - 1 - i));
                }
            }
        }
        return trash;
    }

    public void updateInventoryEntries() {
        for (List<Integer> list : inventoryEntries.values())
            list.clear();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item.isEmpty()) continue;
            for (C category : categorizer.categorize(item))
                inventoryEntries.get(category).add(i);
        }
        for (Map.Entry<C, List<Integer>> entry : inventoryEntries.entrySet()) {
            Comparator<ItemStack> comparator = itemComparators.get(entry.getKey());
            if (comparator != null)
                entry.getValue().sort((i1, i2) -> comparator.compare(inventory.getItem(i1), inventory.getItem(i2)));
        }
    }

    public ItemStack getItem(int i) {
        return inventory.getItem(i);
    }

    public ItemStack getFirstItemInEntry(C cat) {
        return getInventoryEntry(cat).isEmpty() ? ItemStack.EMPTY : inventory.getItem(getInventoryEntry(cat).getFirst());
    }

    public ItemStack getLastItemInEntry(C cat) {
        return getInventoryEntry(cat).isEmpty() ? ItemStack.EMPTY : inventory.getItem(getInventoryEntry(cat).getLast());
    }

    public int getFirstIndexInEntry(C cat) {
        return getInventoryEntry(cat).isEmpty() ? INVALID_INDEX : getInventoryEntry(cat).getFirst();
    }

    public int getLastIndexInEntry(C cat) {
        return getInventoryEntry(cat).isEmpty() ? INVALID_INDEX : getInventoryEntry(cat).getLast();
    }

    public boolean entryNotEmpty(C cat) {
        return !getInventoryEntry(cat).isEmpty();
    }

    public List<Integer> getInventoryEntry(C type) {
        return getInventoryEntries().get(type);
    }

    public Map<C, List<Integer>> getInventoryEntries() {
        return this.inventoryEntries;
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public ItemCategorizer<C> getCategorizer() {
        return this.categorizer;
    }

    public Map<C, Comparator<ItemStack>> getItemComparators() {
        return this.itemComparators;
    }

    public Map<EquipmentSlot, Integer> getEquipmentSlotsInvRefs() {
        return this.equipmentSlotsInvRefs;
    }

}
