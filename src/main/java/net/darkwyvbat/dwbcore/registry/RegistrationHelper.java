package net.darkwyvbat.dwbcore.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public final class RegistrationHelper {

    public static Block registerBlock(ResourceLocation path, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, path);
        return Registry.register(BuiltInRegistries.BLOCK, key, function.apply(properties.setId(key)));
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(ResourceLocation path, FabricBlockEntityTypeBuilder<T> builder) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, path, builder.build());
    }

    public static <T extends Entity> EntityType<T> registerEntity(ResourceLocation path, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, path);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }

    public static Item registerItem(ResourceLocation path, Function<Item.Properties, Item> itemFactory, Item.Properties properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, path);
        return Registry.register(BuiltInRegistries.ITEM, key, itemFactory.apply(properties.setId(key)));
    }

    public static Item registerItem(ResourceLocation path, Function<Item.Properties, Item> itemFactory) {
        return registerItem(path, itemFactory, new Item.Properties());
    }

    public static ResourceKey<PoiType> registerPoi(ResourceLocation path, int ticketCount, int searchDistance, Block... blocks) {
        PointOfInterestHelper.register(path, ticketCount, searchDistance, blocks);
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, path);
    }

    public static ResourceKey<PoiType> registerPoi(ResourceLocation path, int ticketCount, int searchDistance, BlockState... states) {
        PointOfInterestHelper.register(path, ticketCount, searchDistance, Arrays.asList(states));
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, path);
    }

    public static ResourceKey<PoiType> registerPoi(ResourceLocation path, int ticketCount, int searchDistance, Block block, Predicate<BlockState> statePredicate) {
        return registerPoi(path, ticketCount, searchDistance, block.getStateDefinition().getPossibleStates().stream().filter(statePredicate).toArray(BlockState[]::new));
    }
}