package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.darkwyvbat.dwbcore.world.entity.AbstractInventoryHumanoid;
import net.darkwyvbat.dwbcore.world.entity.inventory.DwbItemCategories;
import net.minecraft.world.entity.ai.goal.Goal;

public class HumanoidGoalsCollection {

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