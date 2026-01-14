package net.darkwyvbat.dwbcore.world.entity.inventory;

import net.darkwyvbat.dwbcore.world.entity.inventory.preset.InventoryCleanStrategies;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class InventoryManager {

    public static final int INVALID_INDEX = -1;

    private final SimpleContainer inventory;
    protected final InventoryConfig config;
    protected Map<ItemCategory, List<Integer>> inventoryEntries;

    public InventoryManager(SimpleContainer sourceInventory, ItemCategorizer categorizer, Map<ItemCategory, Comparator<ItemStack>> itemComparators, List<ItemCategory> importanceOrder, Set<ItemCategory> categories) {
        this.inventory = sourceInventory;
        config = InventoryConfig.builder(categorizer)
                .categories(categories)
                .comparators(itemComparators)
                .importanceOrder(importanceOrder)
                .build();
        this.inventoryEntries = new HashMap<>();
        for (ItemCategory category : categories)
            inventoryEntries.put(category, new ArrayList<>());
    }

    public InventoryManager(SimpleContainer sourceInventory, InventoryConfig inventoryConfig) {
        this(sourceInventory, inventoryConfig.categorizer(), inventoryConfig.comparators(), inventoryConfig.importanceOrder(), inventoryConfig.categories());
    }

    public Set<Integer> getPotentialTrash(int slotsCount, Map<ItemCategory, CategoryCollector> categoryCollectStrategies) {
        Set<Integer> trash = new HashSet<>();
        for (ItemCategory category : config.importanceOrder()) {
            if (trash.size() >= slotsCount) break;

            List<Integer> itemsInCategory = inventoryEntries.get(category);
            if (itemsInCategory == null || itemsInCategory.isEmpty()) continue;
            int needFromThisCategory = slotsCount - trash.size();
            if (needFromThisCategory <= 0) break;
            int currentCategorySize = itemsInCategory.size();
            int slotsToFree = Math.min(needFromThisCategory, currentCategorySize);
            CategoryCollector strategy = categoryCollectStrategies != null ? categoryCollectStrategies.getOrDefault(category, InventoryCleanStrategies.FROM_LAST) : InventoryCleanStrategies.FROM_LAST;
            strategy.collect(itemsInCategory, slotsToFree, trash, inventory, category);
        }
        return trash;
    }

    public boolean isCategoryMoreImportant(ItemCategory cat1, ItemCategory cat2) {
        return config.importanceOrder().indexOf(cat1) > config.importanceOrder().indexOf(cat2);
    }

    public ItemCategory getLowestPresentCategory() {
        for (ItemCategory cat : config.importanceOrder())
            if (entryNotEmpty(cat))
                return cat;
        return null;
    }

    public void updateInventoryEntries() {
        for (List<Integer> list : inventoryEntries.values())
            list.clear();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item.isEmpty()) continue;
            for (ItemCategory itemCategory : getCategorizer().categorize(item))
                inventoryEntries.get(itemCategory).add(i);
        }
        for (Map.Entry<ItemCategory, List<Integer>> entry : inventoryEntries.entrySet()) {
            Comparator<ItemStack> comparator = getItemComparators().get(entry.getKey());
            if (comparator != null)
                entry.getValue().sort((i1, i2) -> comparator.compare(inventory.getItem(i1), inventory.getItem(i2)));
        }
    }

    public void addItems(Collection<ItemStack> items, boolean changed) {
        if (items.isEmpty()) return;
        for (ItemStack itemToAdd : items) inventory.addItem(itemToAdd);
        if (changed) inventory.setChanged();
    }

    public void addItems(Collection<ItemStack> items) {
        addItems(items, true);
    }

    public void addItems(ItemStack... itemStacks) {
        addItems(List.of(itemStacks), true);
    }

    public void addItem(ItemStack itemStack) {
        inventory.addItem(itemStack);
    }

    public ItemStack getItem(int i) {
        return inventory.getItem(i);
    }

    public ItemStack getFirstItemInEntry(ItemCategory cat) {
        return entryNotEmpty(cat) ? inventory.getItem(getInventoryEntry(cat).getFirst()) : ItemStack.EMPTY;
    }

    public ItemStack getLastItemInEntry(ItemCategory cat) {
        return entryNotEmpty(cat) ? inventory.getItem(getInventoryEntry(cat).getLast()) : ItemStack.EMPTY;
    }

    public int getFirstIndexInEntry(ItemCategory cat) {
        return entryNotEmpty(cat) ? getInventoryEntry(cat).getFirst() : INVALID_INDEX;
    }

    public int getLastIndexInEntry(ItemCategory cat) {
        return entryNotEmpty(cat) ? getInventoryEntry(cat).getLast() : INVALID_INDEX;
    }

    public int getItemCountInEntry(ItemCategory cat) {
        int c = 0;
        for (int i : getInventoryEntry(cat))
            c += getItem(i).getCount();
        return c;
    }

    public boolean entryNotEmpty(ItemCategory cat) {
        List<Integer> entry = getInventoryEntry(cat);
        return entry != null && !getInventoryEntry(cat).isEmpty();
    }

    public List<Integer> getInventoryEntry(ItemCategory type) {
        return getInventoryEntries().get(type);
    }

    public Map<ItemCategory, List<Integer>> getInventoryEntries() {
        return inventoryEntries;
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    public ItemCategorizer getCategorizer() {
        return config.categorizer();
    }

    public Map<ItemCategory, Comparator<ItemStack>> getItemComparators() {
        return config.comparators();
    }
}
