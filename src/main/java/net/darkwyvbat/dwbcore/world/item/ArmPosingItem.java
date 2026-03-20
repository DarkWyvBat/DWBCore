package net.darkwyvbat.dwbcore.world.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ArmPosingItem {
    HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack itemStack);

    default boolean isTwoHanded(ItemStack stack) {
        return false;
    }
}