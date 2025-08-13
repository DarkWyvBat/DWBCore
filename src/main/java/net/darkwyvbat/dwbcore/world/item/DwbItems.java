package net.darkwyvbat.dwbcore.world.item;

import net.darkwyvbat.dwbcore.world.block.DwbBlocks;
import net.minecraft.world.item.GameMasterBlockItem;
import net.minecraft.world.item.Item;

import static net.minecraft.world.item.Items.registerBlock;

public final class DwbItems {
    public static final Item PROXY_BLOCK = registerBlock(DwbBlocks.PROXY_BLOCK, GameMasterBlockItem::new);

    public static void init() {
    }

}
