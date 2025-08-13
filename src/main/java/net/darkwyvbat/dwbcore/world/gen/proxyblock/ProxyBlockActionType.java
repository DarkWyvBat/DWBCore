package net.darkwyvbat.dwbcore.world.gen.proxyblock;

public record ProxyBlockActionType<A extends ProxyBlockAction>(com.mojang.serialization.MapCodec<A> codec) {
}

