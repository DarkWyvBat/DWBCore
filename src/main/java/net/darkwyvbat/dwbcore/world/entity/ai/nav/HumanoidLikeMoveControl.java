
package net.darkwyvbat.dwbcore.world.entity.ai.nav;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class HumanoidLikeMoveControl extends FeaturedMoveControl {
    private static final double UP = 0.25;
    private static final double DOWN = 0.2;
    private static final double CENTER = 0.2;
    private static final double MAX_HORIZ = 0.1;

    private int state; // 0 none, 1 enter, 2 climb, 3 exit
    private BlockPos climbPos;
    private Direction climbFacing;

    public HumanoidLikeMoveControl(Mob mob) {
        super(mob);
    }

    @Override
    public void tick() {
        state = 0;
        climbPos = null;
        climbFacing = null;

        if (operation == Operation.MOVE_TO) {
            Path path = mob.getNavigation().getPath();
            if (path != null && !path.isDone()) {
                Node next = path.getNextNode(), prev = path.getPreviousNode();
                boolean isNextClimb = next.type == PathType.COCOA;
                boolean isPrevClimb = prev != null && prev.type == PathType.COCOA;
                if (isNextClimb) {
                    state = isPrevClimb ? 2 : 1;
                    climbPos = next.asBlockPos();
                } else if (isPrevClimb) {
                    state = 3;
                    climbPos = prev.asBlockPos();
                }
                if (climbPos != null) {
                    BlockState climbState = mob.level().getBlockState(climbPos);
                    if (climbState.getBlock() instanceof LadderBlock)
                        climbFacing = climbState.getValue(LadderBlock.FACING);
                }
            }
        }
        if (state == 0) {
            super.tick();
            return;
        }
        mob.fallDistance = 0.0F;
        mob.setYRot(rotlerp(mob.getYRot(), getTargetYaw(), 90.0F));
        mob.yBodyRot = mob.getYRot();
        mob.setXRot(0.0F);

        double dY, tX, tZ;
        double dyNorm = wantedY - mob.getY() > 0.05 ? UP : wantedY - mob.getY() < -0.05 ? -DOWN : 0;
        if (state < 3) {
            tX = climbPos.getX() + 0.5;
            tZ = climbPos.getZ() + 0.5;
            if (climbFacing != null) {
                Direction opposite = climbFacing.getOpposite();
                tX -= opposite.getStepX() * 0.2;
                tZ -= opposite.getStepZ() * 0.2;
            }
            if (state == 1 && mob.blockPosition().getY() > climbPos.getY())
                dY = mob.distanceToSqr(tX, mob.getY(), tZ) > 0.02 ? 0.0 : -DOWN;
            else
                dY = dyNorm;
        } else {
            tX = wantedX;
            tZ = wantedZ;
            dY = dyNorm;
        }
        double dX = tX - mob.getX();
        double dZ = tZ - mob.getZ();
        mob.move(MoverType.SELF, new Vec3(Mth.clamp(dX * CENTER, -MAX_HORIZ, MAX_HORIZ), dY, Mth.clamp(dZ * CENTER, -MAX_HORIZ, MAX_HORIZ)));
    }

    private float getTargetYaw() {
        if (state < 3 && climbFacing != null)
            return climbFacing.getOpposite().toYRot();
        double dX = wantedX - mob.getX();
        double dZ = wantedZ - mob.getZ();
        return dX * dX + dZ * dZ > 0.02 ? (float) (Mth.atan2(dZ, dX) * Mth.RAD_TO_DEG) - 90.0F : mob.getYRot();
    }
}
