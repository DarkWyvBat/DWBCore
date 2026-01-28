package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.darkwyvbat.dwbcore.util.time.TickingCooldown;
import net.darkwyvbat.dwbcore.world.entity.AbstractInventoryHumanoid;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.EnumSet;

public class GoToWantedItemGoal extends Goal {
    protected final AbstractInventoryHumanoid mob;
    protected final double speed;
    protected final TickingCooldown cd = new TickingCooldown();

    public GoToWantedItemGoal(AbstractInventoryHumanoid mob, double speedModifier) {
        this.mob = mob;
        this.speed = speedModifier;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return cd.tick() && mob.getWantedItem() != null && mob.getWantedItem().isAlive() && mob.canSelfMove();
    }

    @Override
    public boolean canContinueToUse() {
        return mob.getWantedItem() != null && mob.getWantedItem().isAlive() && mob.canSelfMove() && cd.getTicks() > 100;
    }

    @Override
    public void start() {
        cd.set(300);
        mob.getNavigation().moveTo(mob.getWantedItem(), speed);
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        cd.tick();
        ItemEntity wantedItem = mob.getWantedItem();
        if (wantedItem == null || !wantedItem.isAlive()) {
            mob.getNavigation().stop();
            return;
        }
        mob.getLookControl().setLookAt(wantedItem);
        if (mob.getBoundingBox().intersects(wantedItem.getBoundingBox().inflate(1.3))) {
            if (mob.wantsToPickUp(getServerLevel(mob), wantedItem.getItem())) {
                if (!mob.getInventory().canAddItem(wantedItem.getItem())) {
                    mob.cleanInventory(1);
                    return;
                }
                mob.pickUpItem(getServerLevel(mob), wantedItem);
                cd.reset();
            } else {
                mob.ignoreItem(wantedItem);
                mob.setWantedItem(null);
                cd.set(100);
            }
        }
    }
}