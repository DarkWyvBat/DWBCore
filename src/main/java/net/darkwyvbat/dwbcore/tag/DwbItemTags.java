package net.darkwyvbat.dwbcore.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static net.darkwyvbat.dwbcore.DwbCore.INFO;

public final class DwbItemTags {
    public static final TagKey<Item> MELEE_WEAPONS = TagKey.create(Registries.ITEM, INFO.id("melee_weapons"));
    public static final TagKey<Item> RANGED_WEAPONS = TagKey.create(Registries.ITEM, INFO.id("ranged_weapons"));
}
