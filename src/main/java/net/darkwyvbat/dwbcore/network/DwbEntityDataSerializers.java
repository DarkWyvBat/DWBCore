package net.darkwyvbat.dwbcore.network;

import net.darkwyvbat.dwbcore.DwbCore;
import net.darkwyvbat.dwbcore.world.entity.MobState;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricTrackedDataRegistry;
import net.minecraft.network.syncher.EntityDataSerializer;

public class DwbEntityDataSerializers {
    public static final EntityDataSerializer<MobState> MOB_STATE = EntityDataSerializer.forValueType(MobState.STREAM_CODEC);

    public static void init() {
        FabricTrackedDataRegistry.register(DwbCore.INFO.idOf("mob_state"), MOB_STATE);
    }
}