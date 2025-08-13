package net.darkwyvbat.dwbcore.datagen;

import net.darkwyvbat.dwbcore.tag.DwbBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class DwbCoreBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public DwbCoreBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        valueLookupBuilder(DwbBlockTags.MOB_INTERACTABLE_PASSAGES)
                .forceAddTag(BlockTags.MOB_INTERACTABLE_DOORS)
                .forceAddTag(BlockTags.FENCE_GATES);
    }
}