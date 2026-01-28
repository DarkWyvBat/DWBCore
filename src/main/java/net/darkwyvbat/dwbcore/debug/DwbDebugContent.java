package net.darkwyvbat.dwbcore.debug;

import net.darkwyvbat.dwbcore.DwbCore;
import net.darkwyvbat.dwbcore.client.DwbModelLayers;
import net.darkwyvbat.dwbcore.registry.RegistrationHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class DwbDebugContent {
    private DwbDebugContent() {
    }

    public static final EntityType<HumanoidTester> HUMANOID_TESTER = RegistrationHelper.registerEntity(DwbCore.INFO.id("humanoid_tester"), EntityType.Builder.of(HumanoidTester::new, MobCategory.MISC).sized(0.6F, 1.8F));

    public static void init() {
        DwbCore.LOGGER.debug("DWB Core client debug content");
        FabricDefaultAttributeRegistry.register(HUMANOID_TESTER, HumanoidTester.createAttributes());
    }

    public static void registerClient() {
        DwbCore.LOGGER.debug("DWB Core debug content");
        ModelLayerRegistry.registerModelLayer(DwbModelLayers.HUMANOID_TESTER, HumanoidTesterModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(DwbModelLayers.HUMANOID_TESTER_BABY, () -> HumanoidTesterModel.createBodyLayer().apply(HumanoidModel.BABY_TRANSFORMER));
        EntityRenderers.register(HUMANOID_TESTER, context -> new HumanoidTesterRenderer(context,
                ModelLayers.PLAYER_ARMOR,
                ModelLayers.PLAYER_ARMOR
        ));
    }
}
