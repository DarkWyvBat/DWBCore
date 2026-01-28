package net.darkwyvbat.dwbcore.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkwyvbat.dwbcore.client.DwbModelLayers;
import net.darkwyvbat.dwbcore.client.renderer.entity.HumanoidLikeRenderState;
import net.darkwyvbat.dwbcore.client.renderer.entity.HumanoidLikeRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class HumanoidTesterRenderer extends HumanoidLikeRenderer<HumanoidTester, HumanoidLikeRenderState, HumanoidTesterModel> {

    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/player/wide/steve.png");

    public HumanoidTesterRenderer(EntityRendererProvider.Context context, ArmorModelSet<ModelLayerLocation> innerArmor, ArmorModelSet<ModelLayerLocation> outerArmor) {
        super(context, new HumanoidTesterModel(context.bakeLayer(DwbModelLayers.HUMANOID_TESTER)), new HumanoidTesterModel(context.bakeLayer(DwbModelLayers.HUMANOID_TESTER_BABY)), 0.5F);
        this.addLayer(
                new HumanoidArmorLayer<>(
                        this,
                        ArmorModelSet.bake(innerArmor, context.getModelSet(), HumanoidTesterModel::new),
                        ArmorModelSet.bake(outerArmor, context.getModelSet(), HumanoidTesterModel::new),
                        context.getEquipmentRenderer()
                ));
    }

    protected void scale(HumanoidLikeRenderState state, PoseStack poseStack) {
        float baseScale = 0.9375F;
        poseStack.scale(baseScale, baseScale, baseScale);
    }

    @Override
    public Identifier getTextureLocation(HumanoidLikeRenderState entity) {
        return TEXTURE;
    }

    @Override
    public HumanoidLikeRenderState createRenderState() {
        return new HumanoidLikeRenderState();
    }
}
