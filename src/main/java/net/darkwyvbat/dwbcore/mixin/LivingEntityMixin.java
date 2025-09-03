package net.darkwyvbat.dwbcore.mixin;

import net.darkwyvbat.dwbcore.world.entity.AbstractHumanoidEntity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected abstract EntityDimensions getDefaultDimensions(Pose pose);

    @Shadow
    public abstract float getScale();

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void dwbcore_getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if ((LivingEntity) (Object) this instanceof AbstractHumanoidEntity) {
            cir.setReturnValue(getDefaultDimensions(pose).scale(getScale()));
        }
    }
}