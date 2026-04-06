package net.darkwyvbat.dwbcore.world.entity.ai.nav;

import net.darkwyvbat.dwbcore.world.entity.Crouchable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class HumanoidLikeMoveControl extends HumanoidBaseMoveControl {

    public HumanoidLikeMoveControl(Mob mob) {
        super(mob);
    }

    @Override
    public void tick() {
        if (operation == Operation.MOVE_TO) {
            if (handleScaffold() || handleClimbing(mob.getNavigation().getPath()))
                return;
        }
        super.tick();
    }

    private boolean handleScaffold() {
        Level level = mob.level();
        double speed = mob.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
        BlockPos blockPos = mob.blockPosition();
        if ((level.getBlockState(blockPos).is(Blocks.SCAFFOLDING) || level.getBlockState(blockPos.below()).is(Blocks.SCAFFOLDING)) && wantedY < mob.getY() - 0.5) {
            mob.setShiftKeyDown(true);
            if (mob instanceof Crouchable crouchable)
                crouchable.setCrouch(true);
            mob.setDeltaMovement(mob.getDeltaMovement().x, -speed / 2.0, mob.getDeltaMovement().z);
            mob.setSpeed((float) (speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            double dX = wantedX - mob.getX(), dZ = wantedZ - mob.getZ();
            if (dX * dX + dZ * dZ > 0.05) {
                mob.setYRot(rotlerp(mob.getYRot(), (float) ((Mth.atan2(dZ, dX) * Mth.RAD_TO_DEG) - 90.0F), 90.0F));
                mob.yBodyRot = mob.getYRot();
            }
            return true;
        }
        mob.setShiftKeyDown(false);
        return false;
    }

    private boolean handleClimbing(Path path) {
        if (path == null || path.isDone()) return false;

        Node node = path.getNode(Math.max(0, path.getNextNodeIndex() - 1)), nextNode = path.getNextNode();
        boolean isClimb = HumanoidLikePathNavigation.isClimbNode(node);
        boolean nextIsClimb = HumanoidLikePathNavigation.isClimbNode(nextNode);
        if (!(isClimb || nextIsClimb)) return false;
        if (Math.abs(nextNode.y - mob.getY()) < 0.5 && node.y == nextNode.y || !mob.onClimbable()) return false;

        double dX = wantedX - mob.getX(), dY = wantedY - mob.getY(), dZ = wantedZ - mob.getZ();
        if (mob.onGround() && dY <= 0.2) return false;
        if (dX * dX + dZ * dZ > 0.05) {
            mob.setYRot(rotlerp(mob.getYRot(), (float) (Mth.atan2(dZ, dX) * Mth.RAD_TO_DEG - 90.0F), 90.0F));
            mob.yBodyRot = mob.getYRot();
        }
        mob.setXRot(0.0F);
        Vec3 vec3 = mob.getDeltaMovement();
        double speed = mob.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
        if (isClimb && !nextIsClimb && nextNode.y >= node.y) {
            if (mob.getY() < wantedY) {
                mob.setSpeed(0.0F);
                mob.setDeltaMovement(vec3.x * 0.5, speed / 2.0, vec3.z * 0.5);
            } else {
                mob.setSpeed((float) (speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                mob.setDeltaMovement(Math.clamp(dX, -0.15, 0.15), 0.0, Math.clamp(dZ, -0.15, 0.15));
            }
        } else {
            mob.setSpeed((float) (speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            mob.setDeltaMovement(vec3.x + Math.clamp(dX * 0.05, -0.05, 0.05), dY > 0.1 ? speed / 2.0 : (dY < -0.1 ? -speed / 2.0 : 0.0), vec3.z + Math.clamp(dZ * 0.05, -0.05, 0.05));
        }
        return true;
    }
}