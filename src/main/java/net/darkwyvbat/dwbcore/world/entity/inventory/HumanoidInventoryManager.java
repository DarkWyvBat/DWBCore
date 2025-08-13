package net.darkwyvbat.dwbcore.world.entity.inventory;

import net.darkwyvbat.dwbcore.world.item.ArmorStatsSummary;
import net.darkwyvbat.dwbcore.world.item.ItemUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.*;

public class HumanoidInventoryManager extends InventoryManager<InventoryItemCategory> {

    public static final List<InventoryItemCategory> ITEMS_IMPORTANCE_ORDER;
    public static final Map<InventoryItemCategory, Comparator<ItemStack>> COMPARATORS = new EnumMap<>(InventoryItemCategory.class);
    private final Map<Holder<MobEffect>, Integer> potionEffectCache = new HashMap<>();

    public static final Comparator<ArmorStatsSummary> ARMOR_COMPARATOR = Comparator.comparingDouble(ArmorStatsSummary::protection).thenComparingDouble(ArmorStatsSummary::knockbackResistance);

    static {
        ITEMS_IMPORTANCE_ORDER = List.of(
                InventoryItemCategory.OTHER,
                InventoryItemCategory.RESOURCE,
                InventoryItemCategory.CONSUMABLE,
                InventoryItemCategory.TOOL,
                InventoryItemCategory.SHIELD_OR_SUPPORT,
                InventoryItemCategory.ATTACK_POTION,
                InventoryItemCategory.SUPPORT_POTION,
                InventoryItemCategory.ARMOR,
                InventoryItemCategory.MELEE_WEAPON,
                InventoryItemCategory.RANGED_WEAPON,
                InventoryItemCategory.FAV
        );

        COMPARATORS.put(InventoryItemCategory.MELEE_WEAPON, (i1, i2) -> Double.compare(ItemUtils.getItemAttackDamage(i2), ItemUtils.getItemAttackDamage(i1)));
        COMPARATORS.put(InventoryItemCategory.ARMOR, Comparator.comparing(ItemUtils::getArmorProtection, ARMOR_COMPARATOR).reversed());
        COMPARATORS.put(InventoryItemCategory.CONSUMABLE, Comparator.comparingInt((ItemStack itemStack) -> itemStack.get(DataComponents.FOOD).nutrition() * itemStack.getCount()).reversed());
        COMPARATORS.put(InventoryItemCategory.SUPPORT_POTION, Comparator.comparing(i -> i.is(Items.SPLASH_POTION) || i.is(Items.LINGERING_POTION)));
    }

    public HumanoidInventoryManager(SimpleContainer inventory, ItemCategorizer<InventoryItemCategory> categorizer, Map<InventoryItemCategory, Comparator<ItemStack>> itemComparators, List<InventoryItemCategory> importanceOrder) {
        super(inventory, categorizer, itemComparators, importanceOrder);
        updateInventoryEntries();
    }

    public ItemStack getBestArmorForSlot(EquipmentSlot slot) {
        for (int i : getInventoryEntry(InventoryItemCategory.ARMOR)) {
            ItemStack item = getInventory().getItem(i);
            if (item.get(DataComponents.EQUIPPABLE).slot() == slot)
                return item;
        }
        return ItemStack.EMPTY;
    }

    public boolean hasHeals() {
        return getForHealIndex() != INVALID_INDEX;
    }

    @SuppressWarnings("unchecked")
    public int getForHealIndex() {
        if (entryNotEmpty(InventoryItemCategory.CONSUMABLE))
            return getFirstIndexInEntry(InventoryItemCategory.CONSUMABLE);
        return getPotionWithEffectIndex(MobEffects.INSTANT_HEALTH, MobEffects.REGENERATION);
    }

    private void ensurePotionCacheBuilt() {
        if (!potionEffectCache.isEmpty()) return;
        scanPotionsForCategory(InventoryItemCategory.ATTACK_POTION);
        scanPotionsForCategory(InventoryItemCategory.SUPPORT_POTION);
    }

    private void scanPotionsForCategory(InventoryItemCategory category) {
        for (int i : getInventoryEntry(category)) {
            PotionContents contents = getInventory().getItem(i).get(DataComponents.POTION_CONTENTS);
            if (contents != null)
                for (MobEffectInstance effectInstance : contents.getAllEffects())
                    potionEffectCache.putIfAbsent(effectInstance.getEffect(), i);
        }
    }

    public Map<Holder<MobEffect>, Integer> getAvailablePotionEffectsWithIndices() {
        ensurePotionCacheBuilt();
        return Collections.unmodifiableMap(potionEffectCache);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final int getPotionWithEffectIndex(Holder<MobEffect>... effects) {
        if (effects.length == 0) return INVALID_INDEX;
        ensurePotionCacheBuilt();
        for (Holder<MobEffect> effect : effects) {
            Integer index = potionEffectCache.get(effect);
            if (index != null) return index;
        }
        return INVALID_INDEX;
    }

    @Override
    public void updateInventoryEntries() {
        super.updateInventoryEntries();
        this.potionEffectCache.clear();
    }
}
