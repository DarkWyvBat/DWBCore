package net.darkwyvbat.dwbcore.world.entity.ai.nav;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class MovementHelper {
    public static Vec3 calcRetreat(PathfinderMob mob, Entity target) {
        Vec3 dir = mob.position().subtract(target.position()).normalize();
        return dir.lengthSqr() < 0.01 ? Vec3.directionFromRotation(0, mob.getYRot() + 180.0F) : dir;
    }

    public static void doRetreat(PathfinderMob mob, Vec3 dir) {
        doRetreat(mob, dir, mob.getSpeed());
    }

    public static void doRetreat(PathfinderMob mob, Vec3 dir, double speed) {
        mob.move(MoverType.SELF, dir.scale(speed));
    }

    public static boolean isSafeRetreat(LivingEntity mob, Vec3 dir, double checkDist) {
        BlockPos potentialPos = BlockPos.containing(mob.position().add(dir.scale(checkDist))).below();
        if (!mob.level().getBlockState(potentialPos).isSolidRender() && !mob.level().getBlockState(potentialPos.below()).isSolidRender())
            return false;

        AABB proj = mob.getBoundingBox().move(dir.scale(checkDist));
        for (BlockPos pos : BlockPos.betweenClosed(BlockPos.containing(proj.minX, proj.minY, proj.minZ), BlockPos.containing(proj.maxX, proj.maxY, proj.maxZ))) {
            VoxelShape voxelShape = mob.level().getBlockState(pos).getCollisionShape(mob.level(), pos);
            if (voxelShape.isEmpty()) continue;
            if (Shapes.joinIsNotEmpty(voxelShape.move(pos.getX(), pos.getY(), pos.getZ()), Shapes.create(proj), (a, b) -> b))
                if ((double) pos.getY() + voxelShape.max(Direction.Axis.Y) > proj.minY + mob.maxUpStep())
                    return false;
        }
        return true;
    }

    public static boolean tryPathToTarget(PathfinderMob mob) {
        return tryPathToTarget(mob, 1.0);
    }

    public static boolean tryPathToTarget(PathfinderMob mob, double speed) {
        Path path = mob.getNavigation().createPath(mob.getTarget(), 0);
        if (path == null) return false;
        return mob.getNavigation().moveTo(path, speed);
    }

    public static boolean tryPathAwayTarget(PathfinderMob mob) {
        return tryPathAwayTarget(mob, 1.0, 8, 8);
    }

    public static boolean tryPathAwayTarget(PathfinderMob mob, double speed, int xzSearchR, int ySearchR) {
        Vec3 dir = LandRandomPos.getPosAway(mob, xzSearchR, ySearchR, mob.getTarget().position());
        return dir != null && mob.getNavigation().moveTo(dir.x, dir.y, dir.z, speed);
    }
}
