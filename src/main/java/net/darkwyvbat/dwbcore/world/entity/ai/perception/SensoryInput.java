package net.darkwyvbat.dwbcore.world.entity.ai.perception;

import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Optional;

public record SensoryInput(List<Entity> entitiesAround, Optional<Entity> poiEntity,
                           Optional<Entity> dangerestEntity, int dangerAround) {
}