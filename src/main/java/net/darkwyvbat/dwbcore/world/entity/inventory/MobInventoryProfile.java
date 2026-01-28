package net.darkwyvbat.dwbcore.world.entity.inventory;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.darkwyvbat.dwbcore.world.entity.ai.ItemInspector;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.*;

public record MobInventoryProfile(InventoryConfig inventoryConfig, Map<ItemCategory, ItemInspector> itemInspectors,
                                  Set<ItemStackTemplate> items) {

    public static Builder builder(ItemCategorizer categorizer) {
        return new Builder(InventoryConfig.builder(categorizer));
    }

    public static Builder from(Builder builder) {
        return new Builder(builder);
    }

    public static class Builder {
        private final InventoryConfig.Builder configBuilder;
        private final Map<ItemCategory, ItemInspector> itemInspectors = new Object2ObjectArrayMap<>();
        private final Set<ItemStackTemplate> items = new HashSet<>();

        private Builder(InventoryConfig.Builder configBuilder) {
            this.configBuilder = configBuilder;
        }

        private Builder(Builder source) {
            configBuilder = new InventoryConfig.Builder(source.configBuilder);
            itemInspectors.putAll(source.itemInspectors);
            items.addAll(source.items);
        }

        public Builder category(ItemCategory itemCategory) {
            configBuilder.category(itemCategory);
            return this;
        }

        public Builder categories(ItemCategory... categories) {
            configBuilder.categories(categories);
            return this;
        }

        public Builder comparator(ItemCategory category, Comparator<ItemStack> comparator) {
            configBuilder.comparator(category, comparator);
            return this;
        }

        public Builder cleanStrategy(ItemCategory category, CategoryCollector strategy) {
            configBuilder.cleanStrategy(category, strategy);
            return this;
        }

        public Builder leastImportant(ItemCategory category) {
            configBuilder.leastImportant(category);
            return this;
        }

        public Builder thenImportant(ItemCategory category) {
            configBuilder.then(category);
            return this;
        }

        public Builder inspector(ItemCategory category, ItemInspector inspector) {
            configBuilder.category(category);
            itemInspectors.put(category, inspector);
            return this;
        }

        public Builder item(Item item) {
            items.add(new ItemStackTemplate(item));
            return this;
        }

        public Builder item(ItemStack itemStack) {
            items.add(ItemStackTemplate.fromNonEmptyStack(itemStack));
            return this;
        }

        public Builder item(ItemStackTemplate itemStackTemplate) {
            items.add(itemStackTemplate);
            return this;
        }

        public Builder items(Item... items) {
            for (Item item : items)
                this.items.add(new ItemStackTemplate(item));
            return this;
        }

        public Builder items(ItemStackTemplate... itemStackTemplates) {
            Collections.addAll(items, itemStackTemplates);
            return this;
        }

        public MobInventoryProfile build() {
            return new MobInventoryProfile(configBuilder.build(), Map.copyOf(itemInspectors), Set.copyOf(items));
        }
    }
}