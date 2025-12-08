package net.darkwyvbat.dwbcore.world.entity.ai.nav;

import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.darkwyvbat.dwbcore.lowzone.NodeExtension;
import net.darkwyvbat.dwbcore.world.entity.Crouchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class HumanoidLikeNodeEvaluator extends AmphibiousNodeEvaluator {
    private final Long2BooleanMap crouchCache = new Long2BooleanOpenHashMap();
    private final Long2ObjectMap<DwbPathType> typeCache = new Long2ObjectOpenHashMap<>();
    protected boolean canOpenGates;
    private int cacheReset = 0;

    public HumanoidLikeNodeEvaluator(boolean prefersShallowSwimming) {
        super(prefersShallowSwimming);
    }

    public void setCanOpenGates(boolean canOpen) {
        canOpenGates = canOpen;
    }

    @Override
    public void prepare(PathNavigationRegion region, Mob mob) {
        super.prepare(region, mob);
        mob.setPathfindingMalus(PathType.WATER, 1.0F);
        mob.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0.0F);

        if (mob.getNavigation() instanceof HumanoidLikePathNavigation nav) {
            setCanOpenDoors(nav.canOpenDoors());
            setCanPassDoors(nav.canPassDoors());
            setCanOpenGates(nav.canOpenGates());
        }

        if (++cacheReset > 100) {
            crouchCache.clear();
            typeCache.clear();
            cacheReset = 0;
        }
    }

    @Override
    public @NotNull PathType getPathTypeOfMob(PathfindingContext context, int x, int y, int z, Mob mob) {
        PathType pathType = super.getPathTypeOfMob(context, x, y, z, mob);
        if (pathType != PathType.BLOCKED) return pathType;
        if (!(mob instanceof Crouchable crouchable) || !crouchable.canCrouch()) return PathType.BLOCKED;

        long key = BlockPos.asLong(x, y, z);
        if (crouchCache.containsKey(key)) return crouchCache.get(key) ? PathType.WALKABLE : PathType.BLOCKED;
        int origHeight = entityHeight;
        entityHeight = 1;
        PathType crouchType = super.getPathTypeOfMob(context, x, y, z, mob);
        entityHeight = origHeight;

        boolean canPass = false;
        if (crouchType != PathType.BLOCKED && crouchType != PathType.OPEN) {
            double floorY = getFloorLevel(new BlockPos(x, y, z));
            AABB aabb = crouchable.getCrouchDimension().makeBoundingBox(x + 0.5, floorY, z + 0.5);
            canPass = context.level().noCollision(mob, aabb);
        }
        crouchCache.put(key, canPass);
        return canPass ? crouchType : PathType.BLOCKED;
    }

    @Override
    public @NotNull PathType getPathType(PathfindingContext ctx, int x, int y, int z) {
        long key = BlockPos.asLong(x, y, z);
        if (typeCache.containsKey(key)) return typeCache.get(key).getFallback();

        BlockState state = ctx.getBlockState(new BlockPos(x, y, z));
        DwbPathType type = DwbPathType.NONE;
        PathType vanilla;
        if (state.is(BlockTags.CLIMBABLE)) {
            type = DwbPathTypes.CLIMB;
            vanilla = type.getFallback();
        } else if (canOpenGates && state.getBlock() instanceof FenceGateBlock)
            vanilla = PathType.WALKABLE_DOOR;
        else
            vanilla = super.getPathType(ctx, x, y, z);

        if (type != DwbPathType.NONE) typeCache.put(key, type);
        return vanilla;
    }

    @Override
    public int getNeighbors(Node[] neighbors, Node node) {
        ensureNodeType(node);
        int count = super.getNeighbors(neighbors, node);
        DwbPathType nodeType = ((NodeExtension) node).dwbcore_getType();

        if (nodeType.isClimb()) {
            count = addNode(neighbors, count, getClimbNode(node.x, node.y + 1, node.z));
            count = addNode(neighbors, count, getClimbNode(node.x, node.y - 1, node.z));
            BlockPos pos = node.asBlockPos();
            if (currentContext.getBlockState(pos.above()).isAir())
                count = addNode(neighbors, count, getPotentialNode(node.x, node.y + 1, node.z));
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                Node neighbor = getPotentialNode(node.x + dir.getStepX(), node.y, node.z + dir.getStepZ());
                if (neighbor != null && !((NodeExtension) neighbor).dwbcore_getType().isClimb())
                    count = addNode(neighbors, count, neighbor);
            }
        } else {
            BlockPos pos = node.asBlockPos();
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos check = pos.relative(dir);
                if (currentContext.getBlockState(check).is(BlockTags.CLIMBABLE))
                    count = addNode(neighbors, count, getClimbNode(check.getX(), check.getY(), check.getZ()));
            }
            BlockPos below = pos.below();
            if (currentContext.getBlockState(below).is(BlockTags.CLIMBABLE))
                count = addNode(neighbors, count, getClimbNode(below.getX(), below.getY(), below.getZ()));
        }
        return count;
    }

    private void ensureNodeType(Node node) {
        if (((NodeExtension) node).dwbcore_getType() != DwbPathType.NONE) return;

        long key = BlockPos.asLong(node.x, node.y, node.z);
        if (typeCache.containsKey(key) && typeCache.get(key).isClimb()) {
            ((NodeExtension) node).dwbcore_setType(DwbPathTypes.CLIMB);
            node.costMalus = DwbPathTypes.CLIMB.getMalus();
            return;
        }
        if (currentContext.getBlockState(node.asBlockPos()).is(BlockTags.CLIMBABLE)) {
            ((NodeExtension) node).dwbcore_setType(DwbPathTypes.CLIMB);
            node.costMalus = DwbPathTypes.CLIMB.getMalus();
            typeCache.put(key, DwbPathTypes.CLIMB);
            return;
        }
        if (crouchCache.get(key)) {
            ((NodeExtension) node).dwbcore_setType(DwbPathTypes.CROUCH);
            node.costMalus = DwbPathTypes.CROUCH.getMalus();
        }
    }

    private Node getClimbNode(int x, int y, int z) {
        long key = BlockPos.asLong(x, y, z);
        if (typeCache.get(key) == DwbPathTypes.CLIMB || currentContext.getBlockState(new BlockPos(x, y, z)).is(BlockTags.CLIMBABLE)) {
            Node node = getNode(x, y, z);
            ((NodeExtension) node).dwbcore_setType(DwbPathTypes.CLIMB);
            node.type = DwbPathTypes.CLIMB.getFallback();
            node.costMalus = DwbPathTypes.CLIMB.getMalus();
            return node;
        }
        return null;
    }

    private Node getPotentialNode(int x, int y, int z) {
        PathType type = getCachedPathType(x, y, z);
        long key = BlockPos.asLong(x, y, z);
        boolean isCrouch = crouchCache.get(key);
        if (type == PathType.BLOCKED && isCrouch) type = DwbPathTypes.CROUCH.getFallback();
        if (type != PathType.WALKABLE && type != PathType.OPEN && type != PathType.WALKABLE_DOOR) return null;
        if (type == PathType.OPEN) {
            PathType below = getCachedPathType(x, y - 1, z);
            if (below.getMalus() < 0.0F && below != PathType.WATER && below != PathType.LAVA)
                return null;
        }
        Node node = getNode(x, y, z);
        if (isCrouch) {
            ((NodeExtension) node).dwbcore_setType(DwbPathTypes.CROUCH);
            node.costMalus = DwbPathTypes.CROUCH.getMalus();
        } else {
            ((NodeExtension) node).dwbcore_setType(DwbPathType.NONE);
            node.costMalus = mob.getPathfindingMalus(type);
        }
        node.type = type;
        return node;
    }

    private int addNode(Node[] neighbors, int count, Node node) {
        if (node != null && !node.closed) neighbors[count++] = node;
        return count;
    }
}