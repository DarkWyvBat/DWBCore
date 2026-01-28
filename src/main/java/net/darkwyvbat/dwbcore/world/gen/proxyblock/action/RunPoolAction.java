package net.darkwyvbat.dwbcore.world.gen.proxyblock.action;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkwyvbat.dwbcore.registry.DwbRegistries;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockAction;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockActionType;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockActionTypes;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockPool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.server.level.ServerLevel;

public record RunPoolAction(Holder<ProxyBlockPool> pool) implements ProxyBlockAction {

    public static final MapCodec<RunPoolAction> CODEC = RecordCodecBuilder.mapCodec(i ->
            i.group(
                    RegistryFileCodec.create(DwbRegistries.PROXY_BLOCK_POOL, ProxyBlockPool.CODEC)
                            .fieldOf("pool")
                            .forGetter(RunPoolAction::pool)
            ).apply(i, RunPoolAction::new)
    );

    @Override
    public void execute(ServerLevel level, BlockPos pos, int depth) {
        if (depth > MAX_DEPTH) return;

        pool.value().getRandomAction(level.getRandom()).ifPresentOrElse(action -> action.execute(level, pos, depth + 1), () -> level.setBlock(pos, pool.value().fallback(), 3));
    }

    @Override
    public ProxyBlockActionType<? extends ProxyBlockAction> getType() {
        return ProxyBlockActionTypes.RUN_POOL;
    }
}
