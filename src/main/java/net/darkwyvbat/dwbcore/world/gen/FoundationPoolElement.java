package net.darkwyvbat.dwbcore.world.gen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.Optional;

public class FoundationPoolElement extends SinglePoolElement {
    public static final MapCodec<FoundationPoolElement> CODEC = RecordCodecBuilder.mapCodec(i ->
            i.group(
                    templateCodec(),
                    processorsCodec(),
                    projectionCodec(),
                    overrideLiquidSettingsCodec(),
                    BlockState.CODEC.optionalFieldOf("block").forGetter(e -> Optional.ofNullable(e.blockState)),
                    Codec.INT.optionalFieldOf("layers", 1).forGetter(e -> e.layers)
            ).apply(i, (template, proc, proj, liquid, block, layers) ->
                    new FoundationPoolElement(template, proc, proj, liquid, block.orElse(null), layers)
            )
    );
    private final BlockState blockState;
    private final int layers;

    public FoundationPoolElement(
            Either<Identifier, StructureTemplate> template, Holder<StructureProcessorList> processors,
            StructureTemplatePool.Projection projection,
            Optional<LiquidSettings> liquidSettings,
            BlockState blockState,
            int layers
    ) {
        super(template, processors, projection, liquidSettings);
        this.blockState = blockState;
        this.layers = layers;
    }

    @Override
    public boolean place(
            StructureTemplateManager structureTemplateManager,
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator generator,
            BlockPos position,
            BlockPos referencePos,
            Rotation rotation,
            BoundingBox chunkBB,
            RandomSource random,
            LiquidSettings liquidSettings,
            boolean keepJigsaws
    ) {
        boolean success = super.place(structureTemplateManager, level, structureManager, generator, position, referencePos, rotation, chunkBB, random, liquidSettings, keepJigsaws);
        if (success) {
            StructureTemplate template = structureTemplateManager.getOrCreate(getTemplateLocation());
            StructurePlaceSettings settings = getSettings(rotation, chunkBB, LiquidSettings.IGNORE_WATERLOGGING, false);
            BoundingBox templateBB = template.getBoundingBox(settings, position);
            for (int x = templateBB.minX(); x <= templateBB.maxX(); x++) {
                for (int z = templateBB.minZ(); z <= templateBB.maxZ(); z++) {
                    for (int i = 0; i < layers; i++) {
                        BlockPos currentPos = new BlockPos(x, templateBB.minY() + i, z);
                        if (!chunkBB.isInside(currentPos)) continue;
                        BlockState currentState = level.getBlockState(currentPos);
                        if (!currentState.canBeReplaced()) {
                            BlockPos.MutableBlockPos mutablePos = currentPos.mutable().move(Direction.DOWN);
                            for (int j = 0; j < 100; j++) {
                                if (level.isOutsideBuildHeight(mutablePos) || level.getBlockState(mutablePos).isFaceSturdy(level, mutablePos, Direction.UP))
                                    break;
                                StructureTemplate.StructureBlockInfo originalInfo = new StructureTemplate.StructureBlockInfo(mutablePos, blockState != null ? blockState : currentState, null);
                                StructureTemplate.StructureBlockInfo processedInfo = originalInfo;
                                for (StructureProcessor processor : processors.value().list()) {
                                    processedInfo = processor.processBlock(level, mutablePos, mutablePos, originalInfo, processedInfo, settings);
                                    if (processedInfo == null) break;
                                }
                                if (processedInfo != null) level.setBlock(mutablePos, processedInfo.state(), 2);
                                mutablePos.move(Direction.DOWN);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return success;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return DwbPoolElementTypes.FOUNDATION;
    }

    @Override
    public String toString() {
        return "FoundationPool[" + getTemplateLocation() + "]";
    }
}