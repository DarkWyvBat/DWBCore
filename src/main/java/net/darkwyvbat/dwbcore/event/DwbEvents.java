package net.darkwyvbat.dwbcore.event;

import net.darkwyvbat.dwbcore.world.block.ProxyBlock;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;

import static net.darkwyvbat.dwbcore.registry.DwbRegistries.PROXY_BLOCK_POOL;

public final class DwbEvents {

    public static void init() {
        registerProxyBlockEvents();
    }

    private static void registerProxyBlockEvents() {
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((s, r) -> ProxyBlock.POOLS.clear());
        DynamicRegistrySetupCallback.EVENT.register(r -> r.registerEntryAdded(PROXY_BLOCK_POOL, (rawId, id, pool) -> ProxyBlock.POOLS.put(id.toString(), pool)));
    }
}