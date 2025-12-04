package net.darkwyvbat.dwbcore.world.entity.inventory.preset;

import net.darkwyvbat.dwbcore.world.item.ArmorStatsSummary;
import net.darkwyvbat.dwbcore.world.item.ItemUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Comparator;

public class ItemComparators {
    public static final Comparator<ItemStack> ATTACK_DAMAGE = Comparator.comparingDouble(ItemUtils::getItemAttackDamage).reversed();
    public static final Comparator<ItemStack> BASIC_ARMOR_STATS = Comparator.comparing(ItemUtils::getArmorProtection, Comparator.comparingDouble(ArmorStatsSummary::protection).thenComparingDouble(ArmorStatsSummary::knockbackResistance)).reversed();
    public static final Comparator<ItemStack> SATURATION = Comparator.comparingInt(ItemUtils::getNutrition).reversed();
    public static final Comparator<ItemStack> SUPPORT_POTION_TYPE = Comparator.comparing(i -> i.is(Items.SPLASH_POTION) || i.is(Items.LINGERING_POTION)); //TODO
}