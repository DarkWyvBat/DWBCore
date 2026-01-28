package net.darkwyvbat.dwbcore.world.gen;

import com.mojang.serialization.MapCodec;
import net.darkwyvbat.dwbcore.world.block.ProxyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class ProxyBlockProcessor extends StructureProcessor {
    public static final ProxyBlockProcessor INSTANCE = new ProxyBlockProcessor();

    public static final MapCodec<ProxyBlockProcessor> CODEC = MapCodec.unit(() -> ProxyBlockProcessor.INSTANCE);

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
            LevelReader levelReader,
            BlockPos pos,
            BlockPos pivot,
            StructureTemplate.StructureBlockInfo originalBlockInfo,
            StructureTemplate.StructureBlockInfo currentBlockInfo,
            StructurePlaceSettings structurePlaceSettings
    ) {
        BlockState state = currentBlockInfo.state();
        if (state.getBlock() instanceof ProxyBlock)
            return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), state.setValue(ProxyBlock.EXECUTE_PROPERTY, true), currentBlockInfo.nbt());

        return currentBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return DwbStructureProcessorType.PROXY_BLOCK;
    }
}