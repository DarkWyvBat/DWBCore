package net.darkwyvbat.dwbcore.mixin;

import net.darkwyvbat.dwbcore.world.gen.ProxyBlockProcessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SinglePoolElement.class)
public class SinglePoolElementMixin {

    @Inject(method = "getSettings", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Holder;value()Ljava/lang/Object;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void dwbcore_getSettings(Rotation rot, BoundingBox bb, LiquidSettings ls, boolean kj, CallbackInfoReturnable<StructurePlaceSettings> cir, StructurePlaceSettings sps) {
        sps.addProcessor(ProxyBlockProcessor.INSTANCE);
    }
}