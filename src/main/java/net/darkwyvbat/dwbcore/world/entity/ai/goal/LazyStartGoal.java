package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.darkwyvbat.dwbcore.util.time.Cooldown;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class LazyStartGoal extends Goal {
    private final int checkInterval;
    private final Cooldown cd = new Cooldown();

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
