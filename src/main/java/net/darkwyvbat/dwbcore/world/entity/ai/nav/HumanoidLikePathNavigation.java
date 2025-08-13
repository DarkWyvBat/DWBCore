package net.darkwyvbat.dwbcore.world.entity.ai.nav;

import net.darkwyvbat.dwbcore.world.entity.Crouchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HumanoidLikePathNavigation extends AmphibiousPathNavigation {
    protected boolean canOpenDoors = true;
    protected boolean canPassDoors = true;
    protected boolean canOpenGates = true;

    public HumanoidLikePathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    public void setCanOpenDoors(boolean v) {
        canOpenDoors = v;
    }

    public void setCanPassDoors(boolean v) {
        canPassDoors = v;
    }

    public void setCanOpenGates(boolean v) {
        canOpenGates = v;
        if (nodeEvaluator instanceof HumanoidLikeNodeEvaluator cne)
            cne.setCanOpenGates(v);
    }

    public boolean canOpenDoors() {
        return canOpenDoors;
    }

    public boolean canPassDoors() {
        return canPassDoors;
    }

    public boolean canOpenGates() {
        return canOpenGates;
    }

    @Override
    protected @NotNull PathFinder createPathFinder(int range) {
        HumanoidLikeNodeEvaluator eval = new HumanoidLikeNodeEvaluator(false);
        eval.setCanPassDoors(canPassDoors);
        eval.setCanOpenDoors(canOpenDoors);
        eval.setCanOpenGates(canOpenGates);
        nodeEvaluator = eval;
        return new PathFinder(eval, range);
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        return super.isStableDestination(pos) || level.getBlockState(pos).is(Blocks.LADDER);
    }

    @Override
    protected @NotNull Vec3 getTempMobPos() {
        Vec3 base = super.getTempMobPos();
        BlockPos bp = BlockPos.containing(mob.getX(), mob.getY(), mob.getZ());
        if (level.getBlockState(bp).is(BlockTags.CLIMBABLE))
            return Vec3.atCenterOf(bp);
        if (level.getBlockState(bp).isAir())
            for (Direction dir : Direction.Plane.HORIZONTAL)
                if (level.getBlockState(bp.relative(dir)).is(BlockTags.CLIMBABLE))
                    return Vec3.atCenterOf(bp.relative(dir));
        return base;
    }

    @Override
    public boolean moveTo(@Nullable Path newPath, double speed) {
        if (newPath != null && this.path != null && !this.path.isDone() && !newPath.isDone()) {
            Node oldNext = this.path.getNextNode();
            Node oldPrev = this.path.getPreviousNode();
            if (oldNext.type == PathType.COCOA && newPath.getNextNode().type == PathType.COCOA)
                syncIndexByNode(newPath, oldNext);
            else if (oldPrev != null && oldPrev.type == PathType.COCOA && newPath.getNextNode().type != PathType.COCOA) {
                syncIndexByNode(newPath, oldNext);
                if (newPath.getNextNodeIndex() == 0)
                    syncIndexByNode(newPath, oldPrev);
            }
        }
        return super.moveTo(newPath, speed);
    }

    @Override
    protected void followThePath() {
        if (path == null || path.isDone()) {
            stopNavigation();
            return;
        }

        Vec3 pos = mob.position();
        Node nextNode = path.getNextNode(), prevNode = path.getPreviousNode();
        if (mob instanceof Crouchable crouchable)
            crouchable.setCrouch(nextNode.type == HumanoidLikeNodeEvaluator.CROUCH_PATH_TYPE || prevNode != null && prevNode.type == HumanoidLikeNodeEvaluator.CROUCH_PATH_TYPE);
        boolean nextIsLadder = nextNode.type == PathType.COCOA;
        boolean prevIsLadder = prevNode != null && prevNode.type == PathType.COCOA;
        if (nextIsLadder || prevIsLadder) {
            BlockPos blockPos = BlockPos.containing(pos);
            BlockState blockState = level.getBlockState(blockPos);
            boolean isOnLadder = blockState.is(BlockTags.CLIMBABLE) || (blockState.isAir() && level.getBlockState(blockPos.below()).is(BlockTags.CLIMBABLE));
            boolean readyToAdvance = false;
            Vec3 center = Vec3.atBottomCenterOf(nextNode.asBlockPos());
            double distXZ = pos.distanceToSqr(center.x, pos.y, center.z);

            if (nextIsLadder && !prevIsLadder)
                readyToAdvance = distXZ < 0.4 && Math.abs(pos.y - nextNode.y) < 0.8;
            else if (nextIsLadder && isOnLadder) {
                if (distXZ < 0.4) {
                    if (nextNode.y > prevNode.y)
                        readyToAdvance = pos.y >= nextNode.y - 0.05;
                    else if (nextNode.y < prevNode.y)
                        readyToAdvance = pos.y <= nextNode.y + 0.2;
                    else
                        readyToAdvance = Math.abs(pos.y - nextNode.y) < 0.3;
                }
            } else if (!nextIsLadder) {
                readyToAdvance = mob.position().distanceToSqr(path.getNextEntityPos(mob)) < 0.04;
                if (!readyToAdvance)
                    readyToAdvance = Math.abs(pos.y - WalkNodeEvaluator.getFloorLevel(level, nextNode.asBlockPos())) < 0.1 && (mob.onGround() || !level.getBlockState(nextNode.asBlockPos().below()).isAir());
            }
            if (readyToAdvance) path.advance();
        } else
            super.followThePath();

        if (!isDone()) {
            Vec3 dest = path.getNextEntityPos(mob);
            mob.getMoveControl().setWantedPosition(dest.x, dest.y, dest.z, speedModifier);
        } else
            stopNavigation();
    }

    private void stopNavigation() {
        if (mob.getMoveControl() instanceof FeaturedMoveControl featuredMoveControl) {
            featuredMoveControl.setWantedPosition(mob.getX(), mob.getY(), mob.getZ(), 0);
            featuredMoveControl.setWait();
        } else if (mob.getMoveControl().hasWanted())
            mob.getMoveControl().setWantedPosition(mob.getX(), mob.getY(), mob.getZ(), 0);
    }

    private void syncIndexByNode(Path path, Node targetNode) {
        for (int i = 0; i < path.getNodeCount(); i++) {
            Node node = path.getNode(i);
            if (node.x == targetNode.x && node.y == targetNode.y && node.z == targetNode.z) {
                path.setNextNodeIndex(i);
                break;
            }
        }
    }
}