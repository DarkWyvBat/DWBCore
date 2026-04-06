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
import net.minecraft.world.phys.Vec3;

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

    public boolean canOpenDoors() {
        return canOpenDoors;
    }

    public boolean canPassDoors() {
        return canPassDoors;
    }

    public boolean canOpenGates() {
        return canOpenGates;
    }

    public void setCanOpenGates(boolean v) {
        canOpenGates = v;
        if (nodeEvaluator instanceof HumanoidLikeNodeEvaluator hne)
            hne.setCanOpenGates(v);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        HumanoidLikeNodeEvaluator eval = new HumanoidLikeNodeEvaluator(false);
        eval.setCanPassDoors(canPassDoors);
        eval.setCanOpenDoors(canOpenDoors);
        eval.setCanOpenGates(canOpenGates);
        nodeEvaluator = eval;
        return new PathFinder(eval, maxVisitedNodes);
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        return super.isStableDestination(pos) || level.getBlockState(pos).is(BlockTags.CLIMBABLE);
    }

    @Override
    protected Vec3 getTempMobPos() {
        BlockPos pos = mob.blockPosition();
        if (level.getBlockState(pos).is(BlockTags.CLIMBABLE)) return Vec3.atCenterOf(pos);
        return super.getTempMobPos();
    }

    @Override
    public boolean moveTo(Path newPath, double speedModifier) {
        if (newPath != null && path != null && !path.isDone() && !newPath.isDone()) {
            Node oldNext = path.getNextNode();
            Node oldPrev = path.getPreviousNode();
            if (isClimbNode(oldNext) && isClimbNode(newPath.getNextNode())) {
                syncIndexByNode(newPath, oldNext);
            } else if (isClimbNode(oldPrev) && !isClimbNode(newPath.getNextNode())) {
                syncIndexByNode(newPath, oldNext);
                if (newPath.getNextNodeIndex() == 0)
                    syncIndexByNode(newPath, oldPrev);
            }
        }
        return super.moveTo(newPath, speedModifier);
    }

    @Override
    protected void followThePath() {
        if (path == null || path.isDone()) {
            stopNavigation();
            return;
        }
        if (mob.tickCount % 16 == 0) recoverIndex();

        Vec3 pos = mob.position();
        Node nextNode = path.getNextNode(), prevNode = path.getPreviousNode();
        if (mob instanceof Crouchable crouchable) {
            boolean shouldCrouch = isCrouchNode(nextNode) || isCrouchNode(prevNode);
            if (crouchable.isCrouching() != shouldCrouch)
                crouchable.setCrouch(shouldCrouch);
        }

        if (isClimbNode(nextNode)) {
            double dX = Math.abs(pos.x - (nextNode.x + 0.5));
            double dY = Math.abs(pos.y - nextNode.y);
            double dZ = Math.abs(pos.z - (nextNode.z + 0.5));
            if (dX < 1.0 && dZ < 1.0 && dY < 0.6)
                path.advance();
        } else
            super.followThePath();
        if (!isDone()) {
            Vec3 dest = path.getNextEntityPos(mob);
            mob.getMoveControl().setWantedPosition(dest.x, dest.y, dest.z, speedModifier);
        } else
            stopNavigation();
    }

    private void recoverIndex() {
        int current = path.getNextNodeIndex(), closest = -1;
        int a = Math.max(0, current - 5), b = Math.min(path.getNodeCount(), current + 3);
        double minDistSqr = 16.0;
        for (int i = a; i < b; i++) {
            Node node = path.getNode(i);
            double distSqr = mob.distanceToSqr(node.x + 0.5, node.y, node.z + 0.5);
            if (distSqr < minDistSqr) {
                minDistSqr = distSqr;
                closest = i;
            }
        }
        if (closest == -1) recomputePath();
        else if (closest != current) path.setNextNodeIndex(closest);
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
            if (i >= 0 && i < count && path.getNode(i) == (targetNode)) {
                path.setNextNodeIndex(i);
                return;
            }
            if (offset != 0) {
                i = current - offset;
                if (i >= 0 && i < count && path.getNode(i) == targetNode) {
                    path.setNextNodeIndex(i);
                    return;
                }
            }
        }
        for (int i = 0; i < count; i++) {
            if (path.getNode(i) == targetNode) {
                path.setNextNodeIndex(i);
                return;
            }
        }
    }

    public static boolean isClimbNode(Node node) {
        return node != null && ((NodeExtension) node).dwbcore_getType().isClimb();
    }

    public static boolean isCrouchNode(Node node) {
        return node != null && ((NodeExtension) node).dwbcore_getType().isCrouch();
    }
}