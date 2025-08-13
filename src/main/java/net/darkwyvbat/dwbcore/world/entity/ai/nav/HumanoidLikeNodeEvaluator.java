package net.darkwyvbat.dwbcore.world.entity.ai.nav;

import net.darkwyvbat.dwbcore.world.entity.Crouchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class HumanoidLikeNodeEvaluator extends AmphibiousNodeEvaluator {

    //TODO temp test, reimpl
    public static final PathType CROUCH_PATH_TYPE = PathType.DAMAGE_CAUTIOUS;

    protected boolean canOpenGates;

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
        mob.setPathfindingMalus(PathType.COCOA, 1.0F);
        mob.setPathfindingMalus(CROUCH_PATH_TYPE, 2.0F);
        mob.setPathfindingMalus(PathType.UNPASSABLE_RAIL, 0.0F);

        if (mob.getNavigation() instanceof HumanoidLikePathNavigation nav) {
            setCanOpenDoors(nav.canOpenDoors());
            setCanPassDoors(nav.canPassDoors());
            setCanOpenGates(nav.canOpenGates());
        }
    }

    @Override
    public @NotNull PathType getPathTypeOfMob(PathfindingContext context, int x, int y, int z, Mob mob) {
        PathType pathType = super.getPathTypeOfMob(context, x, y, z, mob);
        if (pathType != PathType.BLOCKED) return pathType;
        if (!(mob instanceof Crouchable crouchableMob) || !crouchableMob.canCrouch()) return PathType.BLOCKED;

        int origHeight = entityHeight;
        entityHeight = 1;
        PathType floorLevelType = super.getPathTypeOfMob(context, x, y, z, mob);
        entityHeight = origHeight;
        if (floorLevelType == PathType.WALKABLE_DOOR || floorLevelType == PathType.FENCE) return floorLevelType;
        AABB aabb = crouchableMob.getCrouchDimension().makeBoundingBox(x + 0.5, y, z + 0.5);
        if (!hasCollision(aabb)) return CROUCH_PATH_TYPE;

        return PathType.BLOCKED;
    }

    private boolean hasCollision(AABB aabb) {
        return !currentContext.level().noCollision(mob, aabb);
    }

    @Override
    public @NotNull PathType getPathType(PathfindingContext ctx, int x, int y, int z) {
        BlockState state = ctx.getBlockState(new BlockPos(x, y, z));
        if (state.is(BlockTags.CLIMBABLE)) return PathType.COCOA;
        if (canOpenGates && state.getBlock() instanceof FenceGateBlock && !state.getValue(FenceGateBlock.OPEN))
            return PathType.WALKABLE_DOOR;
        return super.getPathType(ctx, x, y, z);
    }

    @Override
    public int getNeighbors(Node[] neighbors, Node node) {
        int count = super.getNeighbors(neighbors, node);
        BlockPos pos = node.asBlockPos();
        PathType nodeType = getCachedPathType(node.x, node.y, node.z);

        if (nodeType == PathType.COCOA) {
            count = addNode(neighbors, count, getLadderNode(node.x, node.y + 1, node.z));
            count = addNode(neighbors, count, getLadderNode(node.x, node.y - 1, node.z));
            if (currentContext.getBlockState(pos.above()).isAir())
                count = addNode(neighbors, count, getPotentialNde(node.x, node.y + 1, node.z));

            BlockState state = currentContext.getBlockState(pos);
            if (state.getBlock() instanceof LadderBlock) { //TODO vines
                Direction face = state.getValue(LadderBlock.FACING).getOpposite();
                count = addNode(neighbors, count, getPotentialNde(node.x + face.getStepX(), node.y, node.z + face.getStepZ()));
            }
        } else {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos check = pos.relative(dir);
                if (currentContext.getBlockState(check).is(BlockTags.CLIMBABLE))
                    count = addNode(neighbors, count, getLadderNode(check.getX(), check.getY(), check.getZ()));
            }
            BlockPos below = pos.below();
            if (currentContext.getBlockState(below).is(BlockTags.CLIMBABLE))
                count = addNode(neighbors, count, getLadderNode(below.getX(), below.getY(), below.getZ()));
        }
        return count;
    }

    private Node getLadderNode(int x, int y, int z) {
        if (getCachedPathType(x, y, z) == PathType.COCOA || currentContext.getBlockState(new BlockPos(x, y, z)).is(BlockTags.CLIMBABLE)) {
            Node node = getNode(x, y, z);
            node.type = PathType.COCOA;
            node.costMalus = mob.getPathfindingMalus(PathType.COCOA);
            return node;
        }
        return null;
    }

    private Node getPotentialNde(int x, int y, int z) {
        PathType type = getCachedPathType(x, y, z);
        if (type != PathType.WALKABLE && type != PathType.OPEN && type != CROUCH_PATH_TYPE && type != PathType.WALKABLE_DOOR)
            return null;
        if (type == PathType.OPEN) {
            PathType below = getCachedPathType(x, y - 1, z);
            if (below.getMalus() < 0.0F && below != PathType.WATER && below != PathType.LAVA)
                return null;
        }
        Node node = getNode(x, y, z);
        node.type = type;
        node.costMalus = mob.getPathfindingMalus(type);
        return node;
    }

    private int addNode(Node[] neighbors, int count, Node node) {
        if (node != null && !node.closed) neighbors[count++] = node;
        return count;
    }
}