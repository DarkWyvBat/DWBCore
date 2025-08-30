package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.darkwyvbat.dwbcore.util.PoorRandom;
import net.darkwyvbat.dwbcore.world.entity.AbstractHumanoidEntity;
import net.darkwyvbat.dwbcore.world.entity.AbstractInventoryHumanoid;
import net.darkwyvbat.dwbcore.world.entity.CombatantInventoryHumanoid;
import net.darkwyvbat.dwbcore.world.entity.MobStates;
import net.darkwyvbat.dwbcore.world.entity.ai.perception.ActivityState;
import net.darkwyvbat.dwbcore.world.entity.inventory.InventoryItemCategory;
import net.minecraft.world.entity.ai.goal.Goal;

public class HumanoidGoalsCollection {
    public static class BeOnGuardGoal extends Goal {
        protected final CombatantInventoryHumanoid mob;

        public BeOnGuardGoal(CombatantInventoryHumanoid mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            return (mob.getPerception().getLastScan().dangerAround() > 20 || mob.isAggressive()) && !mob.isUsingItem() && mob.shouldRevisionItems;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            mob.prepareForFight();
            mob.shouldRevisionItems = false;
        }
    }

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

    public static class RandomChillGoal extends LazyStartGoal {
        protected final AbstractHumanoidEntity mob;

        public RandomChillGoal(AbstractHumanoidEntity mob, int checkInterval) {
            super(checkInterval);
            this.mob = mob;
        }

        @Override
        public boolean mainCanUse() {
            return mob.getNavigation().isDone() && mob.getPerception().getProfile().getState().isLess(ActivityState.REGULAR) && mob.onGround() && !mob.isSleeping();
        }

        @Override
        public void start() {
            mob.setMobState(MobStates.SITTING);
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
            return mob.getInventoryManager().getInventoryEntry(InventoryItemCategory.OTHER).size() > mob.getInventory().items.size() / 2;
        }

        @Override
        public void start() {
            mob.cleanInventory(mob.getInventoryManager().getInventoryEntry(InventoryItemCategory.OTHER).size());
        }
    }
}

