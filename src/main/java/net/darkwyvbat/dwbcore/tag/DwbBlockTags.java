package net.darkwyvbat.dwbcore.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import static net.darkwyvbat.dwbcore.DwbCore.INFO;

public class DwbBlockTags {
    public static final TagKey<Block> MOB_INTERACTABLE_PASSAGES = TagKey.create(Registries.BLOCK, INFO.idOf("mob_interactable_passages"));
}
