package net.darkwyvbat.dwbcore.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkwyvbat.dwbcore.client.DwbModelLayers;
import net.darkwyvbat.dwbcore.client.renderer.entity.HumanoidLikeRenderState;
import net.darkwyvbat.dwbcore.client.renderer.entity.HumanoidLikeRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class HumanoidTesterRenderer extends HumanoidLikeRenderer<HumanoidTester, HumanoidLikeRenderState, HumanoidTesterModel> {

    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/player/wide/steve.png");

    public HumanoidTesterRenderer(EntityRendererProvider.Context context, ModelLayerLocation innerArmor, ModelLayerLocation outerArmor, ModelLayerLocation innerBabyArmor, ModelLayerLocation outerBabyArmor) {
        super(context, new HumanoidTesterModel(context.bakeLayer(DwbModelLayers.HUMANOID_TESTER)), new HumanoidTesterModel(context.bakeLayer(DwbModelLayers.HUMANOID_TESTER_BABY)), 0.5F);

        this.addLayer(
                new HumanoidArmorLayer<>(
                        this,
                        new HumanoidArmorModel<>(context.bakeLayer(innerArmor)),
                        new HumanoidArmorModel<>(context.bakeLayer(outerArmor)),
                        new HumanoidArmorModel<>(context.bakeLayer(innerBabyArmor)),
                        new HumanoidArmorModel<>(context.bakeLayer(outerBabyArmor)),
                        context.getEquipmentRenderer()
                ));
    }

    protected void scale(HumanoidLikeRenderState state, PoseStack poseStack) {
        float baseScale = 0.9375F;
        poseStack.scale(baseScale, baseScale, baseScale);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(HumanoidLikeRenderState entity) {
        return TEXTURE;
    }

    @Override
    public @NotNull HumanoidLikeRenderState createRenderState() {
        return new HumanoidLikeRenderState();
    }
}
