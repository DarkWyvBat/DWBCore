package net.darkwyvbat.dwbcore.datagen.lang;

import net.darkwyvbat.dwbcore.advancement.AdvancementInfo;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class TranslationUtils {
    public static void potionTranslation(HolderLookup.Provider provider, FabricLanguageProvider.TranslationBuilder builder, Item item, Function<String, String> function) {
        provider.lookupOrThrow(Registries.POTION).listElements().forEach(p -> {
            if (p.value().getEffects().isEmpty()) return;
            String path = p.key().identifier().getPath();
            builder.add(item.getDescriptionId() + ".effect." + path, function.apply(path));
        });
    }

    public static String titleCase(String text) {
        StringBuilder res = new StringBuilder();
        boolean upNext = true;
        for (char c : text.toLowerCase().toCharArray()) {
            if (c == '_') {
                res.append(' ');
                upNext = true;
            } else {
                res.append(upNext ? Character.toTitleCase(c) : c);
                upNext = false;
            }
        }
        return res.toString();
    }

    public static void advancementTranslation(FabricLanguageProvider.TranslationBuilder builder, AdvancementInfo key, String title, String description) {
        builder.add(key.titleKey(), title);
        builder.add(key.descriptionKey(), description);
    }
}