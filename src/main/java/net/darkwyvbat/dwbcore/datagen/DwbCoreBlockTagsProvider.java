package net.darkwyvbat.dwbcore.datagen;

import net.darkwyvbat.dwbcore.tag.DwbBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class DwbCoreBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {
    public DwbCoreBlockTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        valueLookupBuilder(DwbBlockTags.MOB_INTERACTABLE_PASSAGES)
                .forceAddTag(BlockTags.MOB_INTERACTABLE_DOORS)
                .forceAddTag(BlockTags.FENCE_GATES);
    }
}