package net.darkwyvbat.dwbcore.datagen.lang;

import net.darkwyvbat.dwbcore.world.block.DwbBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class DwbEnglishProvider extends FabricLanguageProvider {

    public DwbEnglishProvider(FabricDataOutput fabricDataOutput, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(fabricDataOutput, "en_us", completableFuture);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder builder) {
        builder.add(DwbBlocks.PROXY_BLOCK, "Proxy Block");
        builder.add("entity.dwbcore.humanoid_tester", "Humanoid Tester");
    }
}