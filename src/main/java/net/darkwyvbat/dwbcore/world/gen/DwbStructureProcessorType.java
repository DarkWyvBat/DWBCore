package net.darkwyvbat.dwbcore.world.gen;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import static net.darkwyvbat.dwbcore.DwbCore.INFO;

public interface DwbStructureProcessorType<P extends StructureProcessor> extends StructureProcessorType<P> {

    StructureProcessorType<ProxyBlockProcessor> PROXY_BLOCK = StructureProcessorType.register(INFO.idOf("proxy_block").toString(), ProxyBlockProcessor.CODEC);

    static void init() {
    }
}
