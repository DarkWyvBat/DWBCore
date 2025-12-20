package net.darkwyvbat.dwbcore.world.gen.proxyblock.action;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockAction;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockActionOps;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockActionType;
import net.darkwyvbat.dwbcore.world.gen.proxyblock.ProxyBlockActionTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.TagValueInput;

import java.util.List;
import java.util.Optional;

public record SpawnEntityAction(EntityType<?> entityType, Optional<CompoundTag> nbt,
                                List<ResourceLocation> ops) implements ProxyBlockAction {

    public static final MapCodec<SpawnEntityAction> CODEC = RecordCodecBuilder.mapCodec(i ->
            i.group(
                    BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(SpawnEntityAction::entityType),
                    TagParser.FLATTENED_CODEC.optionalFieldOf("nbt").forGetter(SpawnEntityAction::nbt),
                    ResourceLocation.CODEC.listOf().optionalFieldOf("ops", List.of()).forGetter(SpawnEntityAction::ops)
            ).apply(i, SpawnEntityAction::new)
    );

    @Override
    public void execute(ServerLevel level, BlockPos pos, int depth) {
        if (depth > MAX_DEPTH) return;

        Entity entity = entityType.create(level, EntitySpawnReason.STRUCTURE);
        if (entity != null) {
            nbt.ifPresent(t -> entity.load(TagValueInput.create(null, level.registryAccess(), t)));
            entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            for (ResourceLocation id : ops)
                ProxyBlockActionOps.run(id, entity);
            if (entity instanceof Mob mob)
                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), EntitySpawnReason.STRUCTURE, null);
            level.addFreshEntity(entity);
            level.removeBlock(pos, true);
        }
    }

    @Override
    public ProxyBlockActionType<? extends ProxyBlockAction> getType() {
        return ProxyBlockActionTypes.SPAWN_ENTITY;
    }
}