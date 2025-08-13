package net.darkwyvbat.dwbcore.debug;

import net.darkwyvbat.dwbcore.client.model.HumanoidLikeModel;
import net.darkwyvbat.dwbcore.client.renderer.entity.HumanoidLikeRenderState;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.RenderType;

public class HumanoidTesterModel extends HumanoidLikeModel<HumanoidLikeRenderState> {
    public HumanoidTesterModel(ModelPart root) {
        super(root, RenderType::entityTranslucent);
    }

    public static LayerDefinition createBodyLayer() {
        CubeDeformation cubeDeformation = CubeDeformation.NONE;
        MeshDefinition meshDefinition = PlayerModel.createMesh(cubeDeformation, false);

        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    protected void setupAttackAnimation(HumanoidLikeRenderState humanRenderState, float f) {
        super.setupAttackAnimation(humanRenderState, f);
    }
}