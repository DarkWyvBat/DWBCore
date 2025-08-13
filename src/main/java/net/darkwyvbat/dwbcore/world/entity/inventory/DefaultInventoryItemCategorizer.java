package net.darkwyvbat.dwbcore.world.entity.inventory;

import net.darkwyvbat.dwbcore.tag.DwbItemTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.EnumSet;
import java.util.Set;

public class DefaultInventoryItemCategorizer implements ItemCategorizer<InventoryItemCategory> {

    @Override
    public Set<InventoryItemCategory> categorize(ItemStack item) {
        Set<InventoryItemCategory> categories = EnumSet.noneOf(InventoryItemCategory.class);

        if (item.is(ItemTags.ARMOR_ENCHANTABLE)) categories.add(InventoryItemCategory.ARMOR);
        if (item.is(DwbItemTags.MELEE_WEAPONS)) categories.add(InventoryItemCategory.MELEE_WEAPON);
        if (item.is(DwbItemTags.RANGED_WEAPONS)) categories.add(InventoryItemCategory.RANGED_WEAPON);
        if (item.is(Items.SHIELD)) categories.add(InventoryItemCategory.SHIELD_OR_SUPPORT);
        if (item.has(DataComponents.POTION_CONTENTS)) categories.add(categorizePotion(item));
        if (item.has(DataComponents.CONSUMABLE)) categories.add(InventoryItemCategory.CONSUMABLE);
        if (categories.isEmpty()) categories.add(InventoryItemCategory.OTHER);

        return categories;
    }

    private InventoryItemCategory categorizePotion(ItemStack item) {
        PotionContents contents = item.get(DataComponents.POTION_CONTENTS);
        if (contents == null || !contents.hasEffects()) return InventoryItemCategory.OTHER;

        boolean hasHarmful = false, hasSupport = false;
        for (MobEffectInstance effect : contents.getAllEffects()) {
            if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) hasHarmful = true;
            if (effect.getEffect().value().getCategory() == MobEffectCategory.BENEFICIAL) hasSupport = true;
            if (hasHarmful && hasSupport) return InventoryItemCategory.OTHER;
        }
        if (hasHarmful && (item.is(Items.SPLASH_POTION) || item.is(Items.LINGERING_POTION)))
            return InventoryItemCategory.ATTACK_POTION;
        if (hasSupport) return InventoryItemCategory.SUPPORT_POTION;

        return InventoryItemCategory.OTHER;
    }

    @Override
    public Class<InventoryItemCategory> getCategoryClass() {
        return InventoryItemCategory.class;
    }
}