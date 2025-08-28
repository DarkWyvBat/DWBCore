package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.darkwyvbat.dwbcore.util.time.TickingCooldown;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class LazyStartGoal extends Goal {
    private final TickingCooldown cd;

    public LazyStartGoal(int checkInterval) {
        cd = new TickingCooldown(checkInterval);
    }

    @Override
    public final boolean canUse() {
        if (!cd.tick()) return false;
        cd.reset();
        return this.mainCanUse();
    }

    public abstract boolean mainCanUse();
}
