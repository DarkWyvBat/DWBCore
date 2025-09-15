package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.darkwyvbat.dwbcore.util.time.TickingCooldown;
import net.darkwyvbat.dwbcore.world.entity.AbstractInventoryHumanoid;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

public class GoToWantedItemGoal extends Goal {
    protected final AbstractInventoryHumanoid mob;
    protected ItemEntity item;
    protected int ticks;
    protected double speed;
    protected final TickingCooldown cd = new TickingCooldown();

    public GoToWantedItemGoal(AbstractInventoryHumanoid mob, double speedModifier) {
        this.mob = mob;
        this.speed = speedModifier;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!cd.tick() || mob.getWantedItems().isEmpty() || !mob.canSelfMove()) return false;

        ItemEntity closestItem = null;
        double closestDistSqr = Double.MAX_VALUE;
        for (ItemEntity itemEntity : mob.getWantedItems()) {
            if (!itemEntity.isAlive()) continue;
            double distSqr = mob.distanceToSqr(itemEntity);
            if (distSqr < closestDistSqr) {
                closestDistSqr = distSqr;
                closestItem = itemEntity;
            }
        }
        if (closestItem == null) return false;
        item = closestItem;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (cd.getTicks() > 200) {
            cd.set(100);
            return false;
        }
        if (item == null || !item.isAlive()) return false;

        double distSqr = mob.distanceToSqr(item);
        if (distSqr < 4.0 && !mob.getInventory().canAddItem(item.getItem())) mob.cleanInventory(1);
        return distSqr > 0.2;
    }

    @Override
    public void start() {
        ticks = 0;
    }

    @Override
    public void stop() {
        item = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (item == null || !item.isAlive()) return;
        mob.getLookControl().setLookAt(item);
        if (ticks % 16 == 0) {
            Path path = mob.getNavigation().createPath(item, 0);
            mob.getNavigation().moveTo(path, speed);
        }
        ++ticks;
    }
}