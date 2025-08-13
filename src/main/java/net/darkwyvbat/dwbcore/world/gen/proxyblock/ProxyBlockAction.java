package net.darkwyvbat.dwbcore.world.gen.proxyblock;

import com.mojang.serialization.Codec;
import net.darkwyvbat.dwbcore.registry.DwbRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;


public interface ProxyBlockAction {
    int MAX_DEPTH = 100;

    Codec<ProxyBlockAction> CODEC = DwbRegistries.PROXY_BLOCK_ACTION_TYPE.byNameCodec().dispatch(ProxyBlockAction::getType, ProxyBlockActionType::codec);

    void execute(ServerLevel level, BlockPos pos, int depth);

    ProxyBlockActionType<? extends ProxyBlockAction> getType();
}
