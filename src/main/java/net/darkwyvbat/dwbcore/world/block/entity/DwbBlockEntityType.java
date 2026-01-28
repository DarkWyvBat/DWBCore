package net.darkwyvbat.dwbcore.world.block.entity;

import net.darkwyvbat.dwbcore.registry.RegistrationHelper;
import net.darkwyvbat.dwbcore.world.block.DwbBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static net.darkwyvbat.dwbcore.DwbCore.INFO;

public final class DwbBlockEntityType {

    public static final BlockEntityType<ProxyBlockEntity> PROXY_BLOCK = RegistrationHelper.registerBlockEntity(INFO.id("proxy_block"), FabricBlockEntityTypeBuilder.create(ProxyBlockEntity::new, DwbBlocks.PROXY_BLOCK));

    public static void init() {
    }
}
