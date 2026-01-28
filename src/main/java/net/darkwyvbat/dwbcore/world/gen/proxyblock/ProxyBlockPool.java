package net.darkwyvbat.dwbcore.world.gen.proxyblock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public record ProxyBlockPool(BlockState fallback, WeightedList<ProxyBlockAction> entries) {

    public static final Codec<ProxyBlockPool> CODEC = RecordCodecBuilder.create(i ->
            i.group(BuiltInRegistries.BLOCK.byNameCodec()
                            .xmap(Block::defaultBlockState, BlockState::getBlock)
                            .optionalFieldOf("fallback", Blocks.AIR.defaultBlockState())
                            .forGetter(ProxyBlockPool::fallback),
                    WeightedList.codec(ProxyBlockAction.CODEC).fieldOf("entries").forGetter(ProxyBlockPool::entries)
            ).apply(i, ProxyBlockPool::new)
    );

    public Optional<ProxyBlockAction> getRandomAction(RandomSource random) {
        return entries.getRandom(random);
    }
}