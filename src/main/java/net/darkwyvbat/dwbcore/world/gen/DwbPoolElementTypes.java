package net.darkwyvbat.dwbcore.world.gen;

import com.mojang.datafixers.util.Either;
import net.darkwyvbat.dwbcore.registry.RegistrationHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.Optional;
import java.util.function.Function;

import static net.darkwyvbat.dwbcore.DwbCore.INFO;


public class DwbPoolElementTypes {
    public static final StructurePoolElementType<FoundationPoolElement> FOUNDATION = RegistrationHelper.registerStructPoolElement(INFO.id("foundation"), FoundationPoolElement.CODEC);

    public static Function<StructureTemplatePool.Projection, FoundationPoolElement> foundation(String id, Holder<StructureProcessorList> processors, BlockState blockState, int layers) {
        return foundation(Identifier.parse(id), processors, blockState, layers);
    }

    public static Function<StructureTemplatePool.Projection, FoundationPoolElement> foundation(Identifier id, Holder<StructureProcessorList> processors, BlockState blockState, int layers) {
        return p -> new FoundationPoolElement(Either.left(id), processors, p, Optional.empty(), blockState, layers);
    }

    public static void init() {
    }
}