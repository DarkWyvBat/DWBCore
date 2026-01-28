package net.darkwyvbat.dwbcore.datagen;

import net.darkwyvbat.dwbcore.tag.DwbItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class DwbCoreItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
    public DwbCoreItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        valueLookupBuilder(DwbItemTags.RANGED_WEAPONS)
                .add(Items.BOW)
                .add(Items.CROSSBOW)
                .add(Items.TRIDENT)
                .add(Items.WIND_CHARGE)
                .add(Items.SNOWBALL);

        valueLookupBuilder(DwbItemTags.MELEE_WEAPONS)
                .forceAddTag(ItemTags.SHARP_WEAPON_ENCHANTABLE)
                .add(Items.TRIDENT);
    }
}
