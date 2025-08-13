package net.darkwyvbat.dwbcore.world.entity.ai.perception;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.List;
import java.util.Optional;

public record SensoryInput(List<Entity> entitiesAround, List<ItemEntity> itemsAround, Optional<Entity> poiEntity,
                           Optional<Entity> dangerestEntity, int dangerAround) {
}