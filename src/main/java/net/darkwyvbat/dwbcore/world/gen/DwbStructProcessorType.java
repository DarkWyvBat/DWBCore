package net.darkwyvbat.dwbcore.world.gen;

import net.darkwyvbat.dwbcore.registry.RegistrationHelper;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import static net.darkwyvbat.dwbcore.DwbCore.INFO;

public class DwbStructProcessorType {

    public static final StructureProcessorType<ProxyBlockProcessor> PROXY_BLOCK = RegistrationHelper.registerStructProcessor(INFO.id("proxy_block"), ProxyBlockProcessor.CODEC);

    public static void init() {
    }
}