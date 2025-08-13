package net.darkwyvbat.dwbcore.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.darkwyvbat.dwbcore.client.model.HumanoidLikeModel;
import net.darkwyvbat.dwbcore.world.entity.AbstractHumanoidEntity;
import net.darkwyvbat.dwbcore.world.entity.MobStates;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public abstract class HumanoidLikeRenderer<T extends AbstractHumanoidEntity, S extends HumanoidLikeRenderState, M extends HumanoidLikeModel<S>> extends HumanoidMobRenderer<T, S, M> {
    public HumanoidLikeRenderer(EntityRendererProvider.Context context, M model, float f) {
        super(context, model, f);
    }

    public HumanoidLikeRenderer(EntityRendererProvider.Context context, M model, M babyModel, float f) {
        super(context, model, babyModel, f, CustomHeadLayer.Transforms.DEFAULT);
    }

    @Override
    protected HumanoidModel.@NotNull ArmPose getArmPose(T entity, HumanoidArm humanoidArm) {
        ItemStack itemStack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack itemStack2 = entity.getItemInHand(InteractionHand.OFF_HAND);
        HumanoidModel.ArmPose armPose = getArmPose(entity, itemStack, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose armPose2 = getArmPose(entity, itemStack2, InteractionHand.OFF_HAND);
        if (armPose.isTwoHanded()) {
            armPose2 = itemStack2.isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        }

        return entity.getMainArm() == humanoidArm ? armPose : armPose2;
    }

    protected static HumanoidModel.ArmPose getArmPose(AbstractHumanoidEntity entity, ItemStack itemStack, InteractionHand interactionHand) {
        if (itemStack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        } else if (!entity.swinging && itemStack.is(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;
        } else {
            if (entity.getUsedItemHand() == interactionHand && entity.getUseItemRemainingTicks() > 0) {
                ItemUseAnimation itemUseAnimation = itemStack.getUseAnimation();
                if (itemUseAnimation == ItemUseAnimation.BLOCK) {
                    return HumanoidModel.ArmPose.BLOCK;
                }

                if (itemUseAnimation == ItemUseAnimation.BOW) {
                    return HumanoidModel.ArmPose.BOW_AND_ARROW;
                }

                if (itemUseAnimation == ItemUseAnimation.SPEAR) {
                    return HumanoidModel.ArmPose.THROW_SPEAR;
                }

                if (itemUseAnimation == ItemUseAnimation.CROSSBOW) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }

                if (itemUseAnimation == ItemUseAnimation.SPYGLASS) {
                    return HumanoidModel.ArmPose.SPYGLASS;
                }

                if (itemUseAnimation == ItemUseAnimation.TOOT_HORN) {
                    return HumanoidModel.ArmPose.TOOT_HORN;
                }

                if (itemUseAnimation == ItemUseAnimation.BRUSH) {
                    return HumanoidModel.ArmPose.BRUSH;
                }
            }

            return HumanoidModel.ArmPose.ITEM;
        }
    }

    @Override
    protected void setupRotations(S state, PoseStack poseStack, float f, float g) {
        float h = state.swimAmount;
        float i = state.xRot;
        if (h > 0.0F) {
            super.setupRotations(state, poseStack, f, g);
            float jx = state.isInWater ? -90.0F - i : -90.0F;
            float k = Mth.lerp(h, 0.0F, jx);
            poseStack.mulPose(Axis.XP.rotationDegrees(k));
            if (state.isVisuallySwimming) {
                poseStack.translate(0.0F, -1.0F, 0.3F);
            }
        } else {
            super.setupRotations(state, poseStack, f, g);
        }
        if (state.isSitting) {
            poseStack.translate(0.0F, -0.55F, 0.0F);
        }
    }

    @Override
    public void extractRenderState(T entity, S state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isSitting = entity.getMobState() == MobStates.SITTING;
    }
}
