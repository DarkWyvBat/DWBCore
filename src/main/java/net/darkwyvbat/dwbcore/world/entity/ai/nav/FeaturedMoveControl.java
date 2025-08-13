package net.darkwyvbat.dwbcore.world.entity.ai.nav;

import net.darkwyvbat.dwbcore.tag.DwbBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FeaturedMoveControl extends MoveControl {
    private static final float YAW_SMOOTH = 0.25F;
    private static final float PITCH_SMOOTH = 0.15F;
    private static final float MAX_YAW_LAND = 50.0F;
    private static final float MAX_YAW_WATER = 40.0F;
    private static final float MAX_PITCH_WATER = 32.0F;
    private static final float H_SWIM = 0.05F;
    private static final float V_SWIM = 0.08F;
    private static final float V_BOOST = 2.0F;
    private static final float JUMP_BOOST = 0.3F;
    private static final double MIN_DIST = 0.1;

    private float targetYaw;
    private float targetPitch;
    private boolean firstUpdate = true;
    private int stuckTicks;

    public FeaturedMoveControl(Mob mob) {
        super(mob);
    }

    @Override
    public void setWantedPosition(double x, double y, double z, double speed) {
        super.setWantedPosition(x, y, z, speed);
        if (operation == Operation.MOVE_TO)
            firstUpdate = true;
    }

    @Override
    public void tick() {
        if (firstUpdate && operation == Operation.MOVE_TO) {
            targetYaw = mob.getYRot();
            targetPitch = mob.getXRot();
            firstUpdate = false;
        }

        float speed = (float) (speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
        if (operation == Operation.MOVE_TO) {
            double dX = wantedX - mob.getX();
            double dY = wantedY - mob.getY();
            double dZ = wantedZ - mob.getZ();
            double distH = Math.sqrt(dX * dX + dZ * dZ);
            if (distH > MIN_DIST)
                targetYaw = Mth.rotLerp(YAW_SMOOTH, targetYaw, (float) (Mth.atan2(dZ, dX) * Mth.RAD_TO_DEG) - 90.0F);
            float maxTurn = mob.isInWater() ? MAX_YAW_WATER : MAX_YAW_LAND;
            mob.setYRot(rotlerp(mob.getYRot(), targetYaw, maxTurn));
            mob.yBodyRot = rotlerp(mob.yBodyRot, mob.getYRot(), MAX_YAW_LAND);

            mob.setSpeed(speed);
            if (mob.isInWater())
                handleWaterMovement(dX, dY, dZ, distH, speed);
            else
                handleLandMovement(dX, dY, dZ);
        } else if (operation == Operation.JUMPING) {
            mob.setSpeed(speed);
            if (mob.onGround() || (mob.isInLiquid() && mob.isAffectedByFluids())) {
                operation = Operation.WAIT;
                stuckTicks = 0;
            }
        } else
            handleIdle();
    }

    private void handleWaterMovement(double dX, double dY, double dZ, double distH, float speed) {
        if ((mob.tickCount % 8 == 0) && mob.getNavigation().getPath() != null && mob.getNavigation().getPath().getNextNode().y > mob.getY() && !mob.isEyeInFluid(FluidTags.WATER))
            mob.getJumpControl().jump();

        float rawPitch = Mth.clamp((float) (Mth.atan2(-dY, distH) * Mth.RAD_TO_DEG), -85.0F, 85.0F);
        targetPitch = Mth.rotLerp(PITCH_SMOOTH, targetPitch, rawPitch);
        if (Math.abs(Mth.degreesDifference(mob.getXRot(), targetPitch)) > 1.0F)
            mob.setXRot(rotlerp(mob.getXRot(), targetPitch, MAX_PITCH_WATER));

        double dist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
        if (dist > 0.001) {
            float vFactor = Math.abs(dY) / (distH + Math.abs(dY)) > 0.7 ? V_SWIM * V_BOOST : V_SWIM;
            Vec3 impulse = new Vec3(dX / dist * speed * H_SWIM, dY / dist * speed * vFactor, dZ / dist * speed * H_SWIM);
            Vec3 nextDelta = mob.getDeltaMovement().add(impulse);
            if (dY > 0.1 && mob.horizontalCollision && ++stuckTicks > 8) {
                nextDelta = nextDelta.add(0, JUMP_BOOST, 0);
                System.out.println("stuck");
                stuckTicks = 0;
            }
            mob.setDeltaMovement(nextDelta.multiply(0.97, 0.96, 0.97));
        }
    }

    private void handleLandMovement(double dX, double dY, double dZ) {
        mob.setXRot(rotlerp(mob.getXRot(), 0.0F, MAX_YAW_LAND / 2F));

        BlockPos pos = mob.blockPosition();
        BlockState state = mob.level().getBlockState(pos);
        VoxelShape shape = state.getCollisionShape(mob.level(), pos);
        if (dY > mob.maxUpStep() && dX * dX + dZ * dZ < Math.max(1.0F, mob.getBbWidth()) || !shape.isEmpty() && mob.getY() < shape.max(Direction.Axis.Y) + pos.getY() && !state.is(DwbBlockTags.MOB_INTERACTABLE_PASSAGES)) {
            mob.getJumpControl().jump();
            operation = Operation.JUMPING;
        } else
            operation = Operation.WAIT;
        stuckTicks = 0;
    }

    private void handleIdle() {
        mob.setSpeed(0.0F);
        if (mob.isInWater()) {
            if ((mob.tickCount % 8 == 0) && mob.distanceToSqr(mob.getNavigation().getTargetPos().getCenter()) < 4.0 && mob.getNavigation().isDone())
                mob.getJumpControl().jump();
            mob.setDeltaMovement(mob.getDeltaMovement().multiply(0.8, 0.8, 0.8));
            targetPitch = Mth.rotLerp(PITCH_SMOOTH, targetPitch, 0.0F);
            mob.setXRot(rotlerp(mob.getXRot(), targetPitch, MAX_PITCH_WATER / 2F));
        } else {
            mob.setZza(0.0F);
            targetPitch = Mth.rotLerp(PITCH_SMOOTH, targetPitch, 0.0F);
            mob.setXRot(rotlerp(mob.getXRot(), targetPitch, MAX_YAW_LAND / 3F));
        }
        firstUpdate = true;
        stuckTicks = 0;
    }
}
