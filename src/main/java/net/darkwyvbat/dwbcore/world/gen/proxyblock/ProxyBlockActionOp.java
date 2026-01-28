package net.darkwyvbat.dwbcore.world.gen.proxyblock;

import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public record ProxyBlockActionOp<T>(Identifier id, Consumer<T> consumer) {
}