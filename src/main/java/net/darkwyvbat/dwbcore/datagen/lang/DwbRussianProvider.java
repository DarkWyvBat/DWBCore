package net.darkwyvbat.dwbcore.datagen.lang;

import net.darkwyvbat.dwbcore.world.block.DwbBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class DwbRussianProvider extends FabricLanguageProvider {

    public DwbRussianProvider(FabricDataOutput fabricDataOutput, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(fabricDataOutput, "ru_ru", completableFuture);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder builder) {
        builder.add(DwbBlocks.PROXY_BLOCK, "Прокси-Блок");
        builder.add("entity.dwbcore.humanoid_tester", "Тестер Гуманоидов");
    }
}