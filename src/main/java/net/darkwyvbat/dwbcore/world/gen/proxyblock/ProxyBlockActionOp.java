package net.darkwyvbat.dwbcore.world.gen.proxyblock;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public record ProxyBlockActionOp<T>(ResourceLocation path, Consumer<T> consumer) {
}