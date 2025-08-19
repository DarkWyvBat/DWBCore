package net.darkwyvbat.dwbcore.registry;

import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockActionType;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockPool;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static net.darkwyvbat.dwbcore.DwbCore.INFO;

public final class DwbRegistries {
    public static final ResourceKey<Registry<ProxyBlockPool>> PROXY_BLOCK_POOL = ResourceKey.createRegistryKey(INFO.idOf("action_pool"));
    public static final ResourceKey<Registry<ProxyBlockActionType<?>>> PROXY_BLOCK_ACTION_TYPE_KEY = ResourceKey.createRegistryKey(INFO.idOf("proxy_block_action_type"));
    public static final Registry<ProxyBlockActionType<?>> PROXY_BLOCK_ACTION_TYPE = FabricRegistryBuilder.createSimple(PROXY_BLOCK_ACTION_TYPE_KEY).buildAndRegister();

    public static void init() {
        DynamicRegistries.register(PROXY_BLOCK_POOL, ProxyBlockPool.CODEC);
    }
}
