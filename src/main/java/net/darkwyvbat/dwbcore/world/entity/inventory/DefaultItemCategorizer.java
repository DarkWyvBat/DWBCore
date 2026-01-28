package net.darkwyvbat.dwbcore.world.entity.inventory;

import net.darkwyvbat.dwbcore.tag.DwbItemTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.HashSet;
import java.util.Set;

public class DefaultItemCategorizer implements ItemCategorizer {

    @Override
    public Set<ItemCategory> categorize(ItemStack itemStack) {
        Set<ItemCategory> categories = new HashSet<>();

        if (itemStack.is(ItemTags.ARMOR_ENCHANTABLE)) categories.add(DwbItemCategories.ARMOR);
        if (itemStack.is(DwbItemTags.MELEE_WEAPONS)) categories.add(DwbItemCategories.MELEE_WEAPON);
        if (itemStack.is(DwbItemTags.RANGED_WEAPONS)) categories.add(DwbItemCategories.RANGED_WEAPON);
        if (itemStack.is(Items.SHIELD)) categories.add(DwbItemCategories.SHIELD_OR_SUPPORT);
        if (itemStack.has(DataComponents.POTION_CONTENTS)) categories.add(categorizePotion(itemStack));
        if (itemStack.has(DataComponents.CONSUMABLE)) categories.add(DwbItemCategories.CONSUMABLE);
        if (categories.isEmpty()) categories.add(DwbItemCategories.OTHER);

        return categories;
    }

    public static ItemCategory categorizePotion(ItemStack item) {
        PotionContents contents = item.get(DataComponents.POTION_CONTENTS);
        if (contents == null || !contents.hasEffects()) return DwbItemCategories.OTHER;

        boolean hasHarmful = false, hasSupport = false;
        for (MobEffectInstance effect : contents.getAllEffects()) {
            if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) hasHarmful = true;
            if (effect.getEffect().value().getCategory() == MobEffectCategory.BENEFICIAL) hasSupport = true;
            if (hasHarmful && hasSupport) return DwbItemCategories.OTHER;
        }
        if (hasHarmful && (item.is(Items.SPLASH_POTION) || item.is(Items.LINGERING_POTION)))
            return DwbItemCategories.ATTACK_POTION;
        if (hasSupport) return DwbItemCategories.SUPPORT_POTION;

        return DwbItemCategories.OTHER;
    }
}