package net.darkwyvbat.dwbcore.world.entity.inventory;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public record InventoryConfig(Set<ItemCategory> categories, ItemCategorizer categorizer,
                              Map<ItemCategory, Comparator<ItemStack>> comparators, List<ItemCategory> importanceOrder,
                              Map<ItemCategory, CategoryCollector> cleanStrategies) {

    public static Builder builder(ItemCategorizer categorizer) {
        return new Builder(categorizer);
    }

    public static class Builder {
        private final ItemCategorizer categorizer;
        private final Set<ItemCategory> categories = new HashSet<>();
        private final Map<ItemCategory, Comparator<ItemStack>> comparators = new Object2ObjectArrayMap<>();
        private final List<ItemCategory> importanceOrder = new ArrayList<>();
        private final Map<ItemCategory, CategoryCollector> cleanStrategies = new Object2ObjectArrayMap<>();

        private Builder(ItemCategorizer categorizer) {
            this.categorizer = Objects.requireNonNull(categorizer);
        }

        Builder(Builder source) {
            categorizer = source.categorizer;
            categories.addAll(source.categories);
            comparators.putAll(source.comparators);
            importanceOrder.addAll(source.importanceOrder);
            cleanStrategies.putAll(source.cleanStrategies);
        }

        public Builder category(ItemCategory category) {
            categories.add(category);
            return this;
        }

        public Builder categories(ItemCategory... categories) {
            Collections.addAll(this.categories, categories);
            return this;
        }

        public Builder categories(Set<ItemCategory> categories) {
            this.categories.addAll(categories);
            return this;
        }

        public Builder comparator(ItemCategory category, Comparator<ItemStack> comparator) {
            comparators.put(category, comparator);
            categories.add(category);
            return this;
        }

        public Builder comparators(Map<ItemCategory, Comparator<ItemStack>> comparators) {
            this.comparators.putAll(comparators);
            categories.addAll(comparators.keySet());
            return this;
        }

        public Builder cleanStrategy(ItemCategory category, CategoryCollector strategy) {
            cleanStrategies.put(category, strategy);
            categories.add(category);
            return this;
        }

        public Builder leastImportant(ItemCategory category) {
            importanceOrder.clear();
            importanceOrder.add(category);
            categories.add(category);
            return this;
        }

        public Builder then(ItemCategory category) {
            importanceOrder.add(category);
            categories.add(category);
            return this;
        }

        public Builder importanceOrder(List<ItemCategory> importanceOrder) {
            this.importanceOrder.addAll(importanceOrder);
            categories.addAll(importanceOrder);
            return this;
        }

        public InventoryConfig build() {
            return new InventoryConfig(Set.copyOf(categories), categorizer, Map.copyOf(comparators), List.copyOf(importanceOrder), Map.copyOf(cleanStrategies));
        }
    }
}