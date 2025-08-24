package net.darkwyvbat.dwbcore.world.entity.ai.nav;

import net.darkwyvbat.dwbcore.world.entity.Crouchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HumanoidLikePathNavigation extends AmphibiousPathNavigation {

    protected boolean canOpenDoors = true;
    protected boolean canPassDoors = true;
    protected boolean canOpenGates = true;
    private BlockPos lastLadderCheckPos = BlockPos.ZERO;
    private boolean lastLadderCheck = false;

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
        if (nodeEvaluator instanceof HumanoidLikeNodeEvaluator hne)
            hne.setCanOpenGates(v);
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
        return super.isStableDestination(pos) || level.getBlockState(pos).is(BlockTags.CLIMBABLE);
    }

    @Override
    protected @NotNull Vec3 getTempMobPos() {
        BlockPos pos = mob.blockPosition();
        if (pos.equals(lastLadderCheckPos))
            return lastLadderCheck ? Vec3.atCenterOf(pos) : super.getTempMobPos();

        lastLadderCheckPos = pos;
        if (level.getBlockState(pos).is(BlockTags.CLIMBABLE)) {
            lastLadderCheck = true;
            return Vec3.atCenterOf(pos);
        }
        if (level.getBlockState(pos).isAir())
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos blockPos = pos.relative(dir);
                if (level.getBlockState(blockPos).is(BlockTags.CLIMBABLE)) {
                    lastLadderCheck = true;
                    return Vec3.atCenterOf(blockPos);
                }
            }
        lastLadderCheck = false;
        return super.getTempMobPos();
    }

    @Override
    public boolean moveTo(@Nullable Path path, double speed) {
        if (path != null && this.path != null && !this.path.isDone() && !path.isDone()) {
            Node oldNext = this.path.getNextNode();
            Node oldPrev = this.path.getPreviousNode();
            if (oldNext.type == PathType.COCOA && path.getNextNode().type == PathType.COCOA)
                syncIndexByNode(path, oldNext);
            else if (oldPrev != null && oldPrev.type == PathType.COCOA && path.getNextNode().type != PathType.COCOA) {
                syncIndexByNode(path, oldNext);
                if (path.getNextNodeIndex() == 0)
                    syncIndexByNode(path, oldPrev);
            }
        }
        return super.moveTo(path, speed);
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
            Vec3 target = nextIsLadder ? Vec3.atBottomCenterOf(nextNode.asBlockPos()) : path.getNextEntityPos(mob);
            if (pos.distanceToSqr(target.x, pos.y, target.z) < (nextIsLadder ? 0.4 : 0.04)) {
                boolean advance;
                if (nextIsLadder && prevIsLadder) {
                    if (nextNode.y > prevNode.y) advance = pos.y >= nextNode.y - 0.05;
                    else if (nextNode.y < prevNode.y) advance = pos.y <= nextNode.y + 0.2;
                    else advance = Math.abs(pos.y - nextNode.y) < 0.3;
                } else if (nextIsLadder)
                    advance = Math.abs(pos.y - nextNode.y) < 0.8;
                else
                    advance = mob.onGround() || Math.abs(pos.y - WalkNodeEvaluator.getFloorLevel(level, nextNode.asBlockPos())) < 0.1;

                if (advance) path.advance();
            }
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
        int count = path.getNodeCount();
        int current = path.getNextNodeIndex();
        for (int offset = 0; offset < 3; ++offset) {
            int i = current + offset;
            if (i >= 0 && i < count && path.getNode(i).equals(targetNode)) {
                path.setNextNodeIndex(i);
                return;
            }
            if (offset != 0) {
                i = current - offset;
                if (i >= 0 && i < count && path.getNode(i).equals(targetNode)) {
                    path.setNextNodeIndex(i);
                    return;
                }
            }
        }
        for (int i = 0; i < count; i++)
            if (path.getNode(i).equals(targetNode)) {
                path.setNextNodeIndex(i);
                return;
            }
    }
}