package net.darkwyvbat.dwbcore.util;

import net.minecraft.resources.Identifier;

public record ModInfo(String namespace, String version, String name) {
    public Identifier id(String id) {
        return Identifier.fromNamespaceAndPath(namespace, id);
    }
}