package net.darkwyvbat.dwbcore.world.gen.proxyblock;

import net.darkwyvbat.dwbcore.world.gen.proxyblock.action.PlaceBlockAction;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.action.RunPoolAction;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.action.SpawnEntityAction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class ProxyBlockPoolBuilder {
    private BlockState fallback = Blocks.AIR.defaultBlockState();
    private final WeightedList.Builder<ProxyBlockAction> entries = WeightedList.builder();

    public ProxyBlockPoolBuilder fallback(Block block) {
        return fallback(block.defaultBlockState());
    }

    public ProxyBlockPoolBuilder fallback(BlockState blockState) {
        fallback = blockState;
        return this;
    }

    public ProxyBlockPoolBuilder entity(EntityType<?> entity, int weight) {
        return entity(entity, null, weight);
    }

    public ProxyBlockPoolBuilder entity(EntityType<?> entity, String nbt, int weight) {
        entries.add(new SpawnEntityAction(entity, parseNbt(nbt)), weight);
        return this;
    }

    public ProxyBlockPoolBuilder block(Block block) {
        return block(block, 1);
    }

    public ProxyBlockPoolBuilder block(Block block, int weight) {
        return block(block.defaultBlockState(), null, false, weight);
    }

    public ProxyBlockPoolBuilder block(Block block, String nbt, int weight) {
        return block(block.defaultBlockState(), nbt, false, weight);
    }

    public ProxyBlockPoolBuilder block(Block block, boolean facing, int weight) {
        return block(block.defaultBlockState(), null, facing, weight);
    }

    public ProxyBlockPoolBuilder block(BlockState state, String nbt, boolean facing, int weight) {
        entries.add(new PlaceBlockAction(state, parseNbt(nbt), facing), weight);
        return this;
    }

    public ProxyBlockPoolBuilder run(Holder<ProxyBlockPool> pool, int weight) {
        entries.add(new RunPoolAction(pool), weight);
        return this;
    }

    public ProxyBlockPool build() {
        return new ProxyBlockPool(fallback, entries.build());
    }

    private static Optional<CompoundTag> parseNbt(String nbt) {
        if (nbt == null || nbt.isEmpty()) return Optional.empty();
        try {
            return Optional.of(TagParser.parseCompoundFully(nbt));
        } catch (Exception e) {
            throw new RuntimeException("ProxyBlock NBT parse failed: " + nbt, e);
        }
    }
}