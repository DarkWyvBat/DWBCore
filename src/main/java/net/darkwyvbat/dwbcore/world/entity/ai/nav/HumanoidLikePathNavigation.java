package net.darkwyvbat.dwbcore.world.entity.ai.nav;

import net.darkwyvbat.dwbcore.lowzone.NodeExtension;
import net.darkwyvbat.dwbcore.world.entity.Crouchable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HumanoidLikePathNavigation extends AmphibiousPathNavigation {
    protected boolean canOpenDoors = true;
    protected boolean canPassDoors = true;
    protected boolean canOpenGates = true;
    private boolean pathCrouchState = false;

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
        if (level.getBlockState(pos).is(BlockTags.CLIMBABLE)) return Vec3.atCenterOf(pos);
        return super.getTempMobPos();
    }

    @Override
    public boolean moveTo(@Nullable Path path, double speed) {
        if (path != null && this.path != null && !this.path.isDone() && !path.isDone()) {
            Node oldNext = this.path.getNextNode();
            Node oldPrev = this.path.getPreviousNode();
            if (isClimbNode(oldNext) && isClimbNode(path.getNextNode()))
                syncIndexByNode(path, oldNext);
            else if (isClimbNode(oldPrev) && !isClimbNode(path.getNextNode())) {
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
        if (mob instanceof Crouchable crouchable && crouchable.isCrouching() == pathCrouchState)
            crouchable.setCrouch(pathCrouchState = isCrouchNode(nextNode) || isCrouchNode(prevNode));
        boolean nextIsClimb = isClimbNode(nextNode);
        boolean prevIsClimb = isClimbNode(prevNode);
        if (nextIsClimb || prevIsClimb) {
            Vec3 target = nextIsClimb ? Vec3.atBottomCenterOf(nextNode.asBlockPos()) : path.getNextEntityPos(mob);
            if (pos.distanceToSqr(target.x, pos.y, target.z) < (nextIsClimb ? 1.0 : 0.1)) {
                boolean advance;
                if (nextIsClimb && prevIsClimb) {
                    if (nextNode.y > prevNode.y) advance = pos.y >= nextNode.y - 0.1;
                    else if (nextNode.y < prevNode.y) advance = pos.y <= nextNode.y + 0.2;
                    else advance = Math.abs(pos.y - nextNode.y) < 0.3;
                } else if (nextIsClimb)
                    advance = Math.abs(pos.y - nextNode.y) < 0.8;
                else
                    advance = mob.onGround() || Math.abs(pos.y - WalkNodeEvaluator.getFloorLevel(level, nextNode.asBlockPos())) < 0.25;

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
        MoveControl moveControl = mob.getMoveControl();
        moveControl.setWantedPosition(mob.getX(), mob.getY(), mob.getZ(), 0);
        moveControl.setWait();
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

    public static boolean isClimbNode(Node node) {
        return node != null && ((NodeExtension) node).dwbcore_getType().isClimb();
    }

    public static boolean isCrouchNode(Node node) {
        return node != null && ((NodeExtension) node).dwbcore_getType().isCrouch();
    }
}