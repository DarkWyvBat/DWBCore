package net.darkwyvbat.dwbcore.world.gen.proxyblock;

import net.darkwyvbat.dwbcore.registry.DwbRegistries;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.action.PlaceBlockAction;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.action.RunPoolAction;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.action.SpawnEntityAction;
import net.minecraft.core.Registry;

import static net.darkwyvbat.dwbcore.DwbCore.INFO;

public final class ProxyBlockActionTypes {
    public static final ProxyBlockActionType<SpawnEntityAction> SPAWN_ENTITY = register("spawn_entity", new ProxyBlockActionType<>(SpawnEntityAction.CODEC));
    public static final ProxyBlockActionType<PlaceBlockAction> PLACE_BLOCK = register("place_block", new ProxyBlockActionType<>(PlaceBlockAction.CODEC));
    public static final ProxyBlockActionType<RunPoolAction> RUN_POOL = register("run_pool", new ProxyBlockActionType<>(RunPoolAction.CODEC));

    private static <A extends ProxyBlockAction> ProxyBlockActionType<A> register(String id, ProxyBlockActionType<A> type) {
        return Registry.register(DwbRegistries.PROXY_BLOCK_ACTION_TYPE, INFO.idOf(id), type);
    }

    public static void init() {
    }
}
