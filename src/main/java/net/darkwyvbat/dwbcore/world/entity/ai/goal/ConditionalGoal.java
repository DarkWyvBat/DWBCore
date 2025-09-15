package net.darkwyvbat.dwbcore.world.entity.ai.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.Predicate;

public abstract class ConditionalGoal<T extends Mob> extends Goal {
    protected final T mob;
    protected final Predicate<T> condition;

    public ConditionalGoal(T mob, Predicate<T> condition) {
        this.mob = mob;
        this.condition = condition;
    }

    @Override
    public boolean canUse() {
        return condition.test(mob);
    }
}