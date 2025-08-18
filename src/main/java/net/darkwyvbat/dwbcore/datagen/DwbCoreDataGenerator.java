package net.darkwyvbat.dwbcore.datagen;

import net.darkwyvbat.dwbcore.datagen.lang.DwbEnglishProvider;
import net.darkwyvbat.dwbcore.datagen.lang.DwbRussianProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DwbCoreDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(DwbCoreModelProvider::new);

        pack.addProvider(DwbCoreItemTagProvider::new);
        pack.addProvider(DwbCoreBlockTagProvider::new);

        addTranslations(pack);
    }

    private static void addTranslations(FabricDataGenerator.Pack pack) {
        pack.addProvider(DwbEnglishProvider::new);
        pack.addProvider(DwbRussianProvider::new);
    }
}

