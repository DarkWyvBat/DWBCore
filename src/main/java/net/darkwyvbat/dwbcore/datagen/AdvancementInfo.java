package net.darkwyvbat.dwbcore.datagen;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public record AdvancementInfo(Identifier id) {
    public String titleKey() {
        return makeKey("title");
    }

    public String descriptionKey() {
        return makeKey("description");
    }

    public Component title() {
        return Component.translatable(titleKey());
    }

    public Component description() {
        return Component.translatable(descriptionKey());
    }

    private String makeKey(String key) {
        return id.toLanguageKey("advancements", key).replace('/', '.');
    }

    public String saveName() {
        return id.toString();
    }
}