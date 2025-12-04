package net.darkwyvbat.dwbcore.world.entity;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Map;
import java.util.Set;

public record ArmorSet(Item helmet, Item chestplate, Item leggings, Item boots) {

    public static final ArmorSet LEATHER_SET = new ArmorSet(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS);
    public static final ArmorSet GOLDEN_SET = new ArmorSet(Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS);
    public static final ArmorSet CHAINMAIL_SET = new ArmorSet(Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS);
    public static final ArmorSet IRON_SET = new ArmorSet(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);
    public static final ArmorSet DIAMOND_SET = new ArmorSet(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS);
    public static final ArmorSet NETHERITE_SET = new ArmorSet(Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS);

    public Map<EquipmentSlot, Item> getPieces() {
        return Map.of(EquipmentSlot.HEAD, helmet, EquipmentSlot.CHEST, chestplate, EquipmentSlot.LEGS, leggings, EquipmentSlot.FEET, boots);
    }

    public Set<ItemStack> asItemStacks() {
        return Set.of(new ItemStack(helmet), new ItemStack(chestplate), new ItemStack(leggings), new ItemStack(boots));
    }
}