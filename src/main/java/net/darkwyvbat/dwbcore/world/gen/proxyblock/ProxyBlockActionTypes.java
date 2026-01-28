package net.darkwyvbat.dwbcore.world.gen.proxyblock;

import net.darkwyvbat.dwbcore.registry.RegistrationHelper;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.action.PlaceBlockAction;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.action.RunPoolAction;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.action.SpawnEntityAction;

import static net.darkwyvbat.dwbcore.DwbCore.INFO;

public final class ProxyBlockActionTypes {
    public static final ProxyBlockActionType<SpawnEntityAction> SPAWN_ENTITY = RegistrationHelper.registerPbActionType(INFO.id("spawn_entity"), new ProxyBlockActionType<>(SpawnEntityAction.CODEC));
    public static final ProxyBlockActionType<PlaceBlockAction> PLACE_BLOCK = RegistrationHelper.registerPbActionType(INFO.id("place_block"), new ProxyBlockActionType<>(PlaceBlockAction.CODEC));
    public static final ProxyBlockActionType<RunPoolAction> RUN_POOL = RegistrationHelper.registerPbActionType(INFO.id("run_pool"), new ProxyBlockActionType<>(RunPoolAction.CODEC));

    public static void init() {
    }
}
