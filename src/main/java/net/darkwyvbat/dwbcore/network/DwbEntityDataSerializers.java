package net.darkwyvbat.dwbcore.network;

import net.darkwyvbat.dwbcore.DwbCore;
import net.darkwyvbat.dwbcore.world.entity.MobState;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry;
import net.minecraft.network.syncher.EntityDataSerializer;

public class DwbEntityDataSerializers {
    public static final EntityDataSerializer<MobState> MOB_STATE = EntityDataSerializer.forValueType(MobState.STREAM_CODEC);

    public static void init() {
        FabricEntityDataRegistry.register(DwbCore.INFO.id("mob_state"), MOB_STATE);
    }
}