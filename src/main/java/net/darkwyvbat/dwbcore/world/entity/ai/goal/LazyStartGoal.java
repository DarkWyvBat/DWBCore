package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.darkwyvbat.dwbcore.util.time.TickingCooldown;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class LazyStartGoal extends Goal {
    private final int checkInterval;
    private final TickingCooldown cd = new TickingCooldown();

    public LazyStartGoal(int checkInterval) {
        this.checkInterval = checkInterval;
        this.cd.set(0);
    }

    @Override
    public final boolean canUse() {
        if (!cd.tick()) return false;
        this.cd.set(checkInterval);
        return this.mainCanUse();
    }

    public abstract boolean mainCanUse();
}
