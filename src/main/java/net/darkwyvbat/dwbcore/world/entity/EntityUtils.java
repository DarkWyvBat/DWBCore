package net.darkwyvbat.dwbcore.world.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class EntityUtils {

    public static boolean isEntityOnFireLine(Entity shooter, double dist, Predicate<Entity> predicate) {
        Vec3 startPos = shooter.getEyePosition();
        Vec3 lookVec = shooter.getViewVector(1.0F);
        AABB aabb = shooter.getBoundingBox().expandTowards(lookVec.scale(dist)).inflate(1.0);
        return ProjectileUtil.getEntityHitResult(shooter, startPos, startPos.add(lookVec.scale(dist)), aabb, predicate, dist * dist) != null;
    }

    public static List<Entity> getEntitiesInAABB(LivingEntity entity, int dist) {
        return getEntitiesInAABB(entity, dist, false);
    }

    public static List<Entity> getEntitiesInAABB(LivingEntity entity, int dist, boolean canSee) {
        if (entity == null) return new ArrayList<>();
        AABB aabb = new AABB(entity.getX() - dist, entity.getY() - dist, entity.getZ() - dist, entity.getX() + dist, entity.getY() + dist, entity.getZ() + dist);
        return entity.level().getEntitiesOfClass(Entity.class, aabb, e -> (!canSee || entity.hasLineOfSight(e)) && e != entity);
    }
}
