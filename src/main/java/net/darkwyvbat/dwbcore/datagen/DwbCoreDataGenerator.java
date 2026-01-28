package net.darkwyvbat.dwbcore.datagen;

import net.darkwyvbat.dwbcore.datagen.lang.DwbEnglishProvider;
import net.darkwyvbat.dwbcore.datagen.lang.DwbRussianProvider;
import net.darkwyvbat.dwbcore.debug.TestProxPools;
import net.darkwyvbat.dwbcore.registry.DwbRegistries;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;

public class DwbCoreDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(DwbCoreModelProvider::new);

        pack.addProvider(DwbCoreItemTagsProvider::new);
        pack.addProvider(DwbCoreBlockTagsProvider::new);

        pack.addProvider((o, r) -> new FabricDynamicRegistryProvider(o, r) {
            @Override
            protected void configure(HolderLookup.Provider registries, Entries entries) {
                entries.addAll(registries.lookupOrThrow(DwbRegistries.PROXY_BLOCK_POOL));
            }

            @Override
            public String getName() {
                return "ProxyBlock Pools";
            }
        });

        addTranslations(pack);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder builder) {
        builder.add(DwbRegistries.PROXY_BLOCK_POOL, TestProxPools::bootstrap);
    }

    private static void addTranslations(FabricDataGenerator.Pack pack) {
        pack.addProvider(DwbEnglishProvider::new);
        pack.addProvider(DwbRussianProvider::new);
    }
}