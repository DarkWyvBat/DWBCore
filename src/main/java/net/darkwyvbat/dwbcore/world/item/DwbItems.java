package net.darkwyvbat.dwbcore.world.item;

import net.darkwyvbat.dwbcore.registry.RegistrationHelper;
import net.darkwyvbat.dwbcore.world.block.DwbBlocks;
import net.minecraft.world.item.GameMasterBlockItem;
import net.minecraft.world.item.Item;

public final class DwbItems {
    public static final Item PROXY_BLOCK = RegistrationHelper.registerBlockItem(DwbBlocks.PROXY_BLOCK, GameMasterBlockItem::new);

    public static void init() {
    }

}