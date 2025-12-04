package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.darkwyvbat.dwbcore.util.PoorRandom;
import net.darkwyvbat.dwbcore.world.entity.AbstractHumanoidEntity;
import net.darkwyvbat.dwbcore.world.entity.AbstractInventoryHumanoid;
import net.darkwyvbat.dwbcore.world.entity.ai.perception.ActivityState;
import net.darkwyvbat.dwbcore.world.entity.inventory.DwbItemCategories;
import net.minecraft.world.entity.ai.goal.Goal;

public class HumanoidGoalsCollection {

    public static class RandomDisarm extends LazyStartGoal {
        protected final AbstractHumanoidEntity mob;

        public RandomDisarm(AbstractHumanoidEntity mob, int checkInterval) {
            super(checkInterval);
            this.mob = mob;
        }

        @Override
        public boolean mainCanUse() {
            return mob.getPerception().getProfile().getState().isLess(ActivityState.REGULAR) && PoorRandom.quickProb(0.001F);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            mob.disarm();
        }
    }

    public static class RandomFreeHandsGoal extends Goal {
        protected final AbstractHumanoidEntity mob;

        public RandomFreeHandsGoal(AbstractHumanoidEntity mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            return mob.getPerception().getProfile().getAttitude() < 5 && PoorRandom.quickProb(0.01F) && !mob.isUsingItem();
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            mob.freeHands();
        }
    }

    public static class OptimizeInventory extends Goal {
        private final AbstractInventoryHumanoid mob;

        public OptimizeInventory(AbstractInventoryHumanoid mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            return mob.getInventoryManager().getInventoryEntry(DwbItemCategories.OTHER).size() > mob.getInventory().items.size() / 2;
        }

        @Override
        public void start() {
            mob.cleanInventory(mob.getInventoryManager().getInventoryEntry(DwbItemCategories.OTHER).size());
        }
    }
}