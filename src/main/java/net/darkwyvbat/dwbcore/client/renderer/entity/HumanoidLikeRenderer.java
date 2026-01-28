package net.darkwyvbat.dwbcore.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.darkwyvbat.dwbcore.client.model.HumanoidLikeModel;
import net.darkwyvbat.dwbcore.world.entity.AbstractHumanoidEntity;
import net.darkwyvbat.dwbcore.world.entity.MobState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public abstract class HumanoidLikeRenderer<T extends AbstractHumanoidEntity, S extends HumanoidLikeRenderState, M extends HumanoidLikeModel<S>> extends HumanoidMobRenderer<T, S, M> {

    public HumanoidLikeRenderer(EntityRendererProvider.Context context, M model, M babyModel, float f) {
        super(context, model, babyModel, f, CustomHeadLayer.Transforms.DEFAULT);
    }

    @Override
    protected HumanoidModel.ArmPose getArmPose(T entity, HumanoidArm humanoidArm) {
        ItemStack mainHandItem = entity.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHandItem = entity.getItemInHand(InteractionHand.OFF_HAND);
        HumanoidModel.ArmPose mainHandPose = getArmPose(entity, mainHandItem, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose offHandPose = getArmPose(entity, offHandItem, InteractionHand.OFF_HAND);
        if (mainHandPose.isTwoHanded())
            offHandPose = offHandItem.isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;

        return entity.getMainArm() == humanoidArm ? mainHandPose : offHandPose;
    }

    protected static HumanoidModel.ArmPose getArmPose(AbstractHumanoidEntity entity, ItemStack itemStack, InteractionHand interactionHand) {
        if (itemStack.isEmpty())
            return HumanoidModel.ArmPose.EMPTY;
        if (!entity.swinging && itemStack.is(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack))
            return HumanoidModel.ArmPose.CROSSBOW_HOLD;

        if (entity.getUsedItemHand() == interactionHand && entity.getUseItemRemainingTicks() > 0) {
            return switch (itemStack.getUseAnimation()) {
                case BLOCK -> HumanoidModel.ArmPose.BLOCK;
                case BOW -> HumanoidModel.ArmPose.BOW_AND_ARROW;
                case TRIDENT -> HumanoidModel.ArmPose.THROW_TRIDENT;
                case SPEAR -> HumanoidModel.ArmPose.SPEAR;
                case CROSSBOW -> HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                case SPYGLASS -> HumanoidModel.ArmPose.SPYGLASS;
                case TOOT_HORN -> HumanoidModel.ArmPose.TOOT_HORN;
                case BRUSH -> HumanoidModel.ArmPose.BRUSH;
                default -> HumanoidModel.ArmPose.ITEM;
            };
        }

        return HumanoidModel.ArmPose.ITEM;
    }

    @Override
    protected void setupRotations(S state, PoseStack poseStack, float f, float g) {
        super.setupRotations(state, poseStack, f, g);
        if (state.swimAmount > 0.0F) {
            float swimPitch = state.isInWater ? -90.0F - state.xRot : -90.0F;
            float pitch = Mth.lerp(state.swimAmount, 0.0F, swimPitch);
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
            if (state.isVisuallySwimming) {
                poseStack.translate(0.0F, -1.0F, 0.3F);
            }
        }
        if (state.isSitting) {
            poseStack.translate(0.0F, -0.55F, 0.0F);
        }
    }

    @Override
    public void extractRenderState(T entity, S state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isSitting = entity.getMobState() == MobState.SITTING;
    }
}