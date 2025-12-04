package net.darkwyvbat.dwbcore.world.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.Equippable;

import java.util.Map;

public final class ItemUtils {

    public static ArmorStatsSummary getArmorProtection(ItemStack itemStack) {
        if (itemStack.isEmpty()) return ArmorStatsSummary.EMPTY;
        ArmorStatsSummary stats = getBaseArmorProtection(itemStack);
        return new ArmorStatsSummary(stats.protection() + getEnchantmentsProtection(itemStack), stats.knockbackResistance());
    }

    public static ArmorStatsSummary getBaseArmorProtection(ItemStack itemStack) {
        if (itemStack.isEmpty()) return ArmorStatsSummary.EMPTY;
        final Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || !isArmorSlot(equippable.slot()))
            return ArmorStatsSummary.EMPTY;

        double protection = 0.0, knockbackResistance = 0.0;
        ItemAttributeModifiers modifiers = itemStack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (!entry.slot().test(equippable.slot()) || entry.modifier().operation() != AttributeModifier.Operation.ADD_VALUE)
                continue;
            if (isAttribute(entry.attribute(), Attributes.ARMOR))
                protection += entry.modifier().amount();
            else if (isAttribute(entry.attribute(), Attributes.KNOCKBACK_RESISTANCE))
                knockbackResistance += entry.modifier().amount();
        }
        return new ArmorStatsSummary(protection, knockbackResistance);
    }

    public static int getEnchantmentsProtection(ItemStack itemStack) {
        if (itemStack.isEmpty()) return 0;
        return itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).entrySet().stream().filter(e -> e.getKey().is(Enchantments.PROTECTION)).findFirst().map(Map.Entry::getValue).orElse(0);
    }

    public static double getItemAttackDamage(ItemStack item) {
        if (item.isEmpty()) return 0;
        return item.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY)
                .modifiers().stream().filter(e -> e.attribute().is(Attributes.ATTACK_DAMAGE.unwrapKey().orElseThrow(() -> new IllegalStateException("No attack damage key"))))
                .findFirst()
                .map(e -> e.modifier().amount())
                .orElse(0.0);
    }

    public static boolean isArmorSlot(EquipmentSlot slot) {
        EquipmentSlot.Type type = slot.getType();
        return type == EquipmentSlot.Type.HUMANOID_ARMOR || type == EquipmentSlot.Type.ANIMAL_ARMOR;
    }

    private static boolean isAttribute(Holder<Attribute> a, Holder<Attribute> b) {
        return a.is(b.unwrapKey().orElseThrow());
    }

    public static int getNutrition(ItemStack itemStack) {
        FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
        return foodProperties == null ? 0 : foodProperties.nutrition();
    }
}