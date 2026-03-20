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
            LevelReader level,
            BlockPos targetPosition,
            BlockPos referencePos,
            StructureTemplate.StructureBlockInfo originalBlockInfo,
            StructureTemplate.StructureBlockInfo processedBlockInfo,
            StructurePlaceSettings settings
    ) {
        BlockState state = processedBlockInfo.state();
        if (state.getBlock() instanceof ProxyBlock)
            return new StructureTemplate.StructureBlockInfo(processedBlockInfo.pos(), state.setValue(ProxyBlock.EXECUTE_PROPERTY, true), processedBlockInfo.nbt());

        return processedBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return DwbStructProcessorType.PROXY_BLOCK;
    }
}