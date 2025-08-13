package net.darkwyvbat.dwbcore.util;

import net.minecraft.resources.ResourceLocation;

public record ModInfo(String id, String version, String name) {
    public ResourceLocation idOf(String path) {
        return ResourceLocation.fromNamespaceAndPath(id, path);
    }
}