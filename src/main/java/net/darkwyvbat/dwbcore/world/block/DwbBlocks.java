package net.darkwyvbat.dwbcore.world.block;

import net.darkwyvbat.dwbcore.registry.RegistrationHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import static net.darkwyvbat.dwbcore.DwbCore.INFO;

public final class DwbBlocks {

    public static final Block PROXY_BLOCK = RegistrationHelper.registerBlock(
            INFO.idOf("proxy_block"),
            ProxyBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(-1.0F, 9999)
                    .sound(SoundType.STONE)
                    .noOcclusion()
    );

    public static void init() {
    }
}
