package net.darkwyvbat.dwbcore.world.entity.ai.nav;

import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import net.darkwyvbat.dwbcore.lowzone.NodeExtension;
import net.darkwyvbat.dwbcore.world.entity.Crouchable;
import net.minecraft.core.BlockPos;
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

public class HumanoidLikeNodeEvaluator extends AmphibiousNodeEvaluator {
    private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
    private final Long2BooleanMap crouchCache = new Long2BooleanOpenHashMap();
    protected boolean canOpenGates;

    public HumanoidLikeNodeEvaluator(boolean prefersShallowSwimming) {
        super(prefersShallowSwimming);
    }

    public void setCanOpenGates(boolean canOpen) {
        canOpenGates = canOpen;
    }

    @Override
    public void prepare(PathNavigationRegion level, Mob mob) {
        super.prepare(level, mob);
        mob.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0.0F);

        if (mob.getNavigation() instanceof HumanoidLikePathNavigation nav) {
            setCanOpenDoors(nav.canOpenDoors());
            setCanPassDoors(nav.canPassDoors());
            setCanOpenGates(nav.canOpenGates());
        }
    }

    @Override
    public void done() {
        super.done();
        crouchCache.clear();
    }

    private boolean isClimbable(int x, int y, int z) {
        return currentContext.getBlockState(mutablePos.set(x, y, z)).is(BlockTags.CLIMBABLE);
    }

    @Override
    public Node getStart() {
        BlockPos blockPos = mob.blockPosition();
        if (isClimbable(blockPos.getX(), blockPos.getY(), blockPos.getZ())) {
            Node start = getNode(blockPos);
            ((NodeExtension) start).dwbcore_setType(DwbPathTypes.CLIMB);
            start.type = DwbPathTypes.CLIMB.getFallback();
            start.costMalus = DwbPathTypes.CLIMB.getMalus();
            return start;
        }
        return super.getStart();
    }

    @Override
    public PathType getPathTypeOfMob(PathfindingContext context, int x, int y, int z, Mob mob) {
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
            if (!context.getBlockState(mutablePos.set(x, y + 1, z)).isCollisionShapeFullBlock(context.level(), mutablePos)) {
                double floorY = getFloorLevel(mutablePos.set(x, y, z));
                AABB aabb = crouchable.getCrouchDimension().makeBoundingBox(x + 0.5, floorY, z + 0.5);
                canPass = context.level().noCollision(mob, aabb);
            }
        }
        crouchCache.put(key, canPass);
        return canPass ? crouchType : PathType.BLOCKED;
    }

    @Override
    public PathType getPathType(PathfindingContext context, int x, int y, int z) {
        BlockState state = context.getBlockState(mutablePos.set(x, y, z));
        if (state.is(BlockTags.CLIMBABLE)) return DwbPathTypes.CLIMB.getFallback();
        else if (canOpenGates && state.getBlock() instanceof FenceGateBlock) return PathType.WALKABLE_DOOR;
        return super.getPathType(context, x, y, z);
    }

    @Override
    public int getNeighbors(Node[] neighbors, Node node) {
        setNodeType(node);
        int count = super.getNeighbors(neighbors, node);
        DwbPathType nodeType = ((NodeExtension) node).dwbcore_getType();

        if (nodeType.isClimb()) {
            Node upNode = getClimbNode(node.x, node.y + 1, node.z);
            count = addNode(neighbors, count, upNode != null ? upNode : getPotentialNode(node.x, node.y + 1, node.z));
        } else if (isClimbable(node.x, node.y + 1, node.z)) {
            Node upNode = getClimbNode(node.x, node.y + 1, node.z);
            count = addNode(neighbors, count, upNode);
        }

        for (int i = 0; i < count; i++) {
            Node neighbor = neighbors[i];
            if (isClimbable(neighbor.x, neighbor.y, neighbor.z)) {
                long key = BlockPos.asLong(neighbor.x, neighbor.y, neighbor.z);
                if (crouchCache.get(key)) {
                    ((NodeExtension) neighbor).dwbcore_setType(DwbPathTypes.CROUCH);
                    neighbor.costMalus = Math.max(neighbor.costMalus, DwbPathTypes.CROUCH.getMalus());
                } else {
                    ((NodeExtension) neighbor).dwbcore_setType(DwbPathTypes.CLIMB);
                    neighbor.costMalus = Math.max(neighbor.costMalus, DwbPathTypes.CLIMB.getMalus());
                }
            }
        }
        return addNode(neighbors, count, getClimbNode(node.x, node.y - 1, node.z));
    }

    private void setNodeType(Node node) {
        if (((NodeExtension) node).dwbcore_getType() != DwbPathType.NONE) return;

        long key = BlockPos.asLong(node.x, node.y, node.z);
        boolean isClimb = isClimbable(node.x, node.y, node.z);
        boolean isCrouch = crouchCache.get(key);
        if (isClimb && isCrouch) {
            ((NodeExtension) node).dwbcore_setType(DwbPathTypes.HUMANOID);
            node.costMalus = DwbPathTypes.HUMANOID.getMalus();
        } else if (isClimb) {
            ((NodeExtension) node).dwbcore_setType(DwbPathTypes.CLIMB);
            node.costMalus = DwbPathTypes.CLIMB.getMalus();
        } else if (isCrouch) {
            ((NodeExtension) node).dwbcore_setType(DwbPathTypes.CROUCH);
            node.costMalus = DwbPathTypes.CROUCH.getMalus();
        }
    }

    private Node getClimbNode(int x, int y, int z) {
        if (!isClimbable(x, y, z)) return null;
        Node node = getNode(x, y, z);
        ((NodeExtension) node).dwbcore_setType(DwbPathTypes.CLIMB);
        node.type = DwbPathTypes.CLIMB.getFallback();
        node.costMalus = DwbPathTypes.CLIMB.getMalus();
        return node;
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