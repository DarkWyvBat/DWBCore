package net.darkwyvbat.dwbcore.world.entity.ai.combat;

public abstract class CombatStrategy {
    public void start(CombatState state, CombatStrategy prevStrategy) {
    }

    public void stop(CombatState state, CombatStrategy nextStrategy) {
    }

    public void tick(CombatState state) {
    }

    public abstract boolean canStart(CombatStateView state, CombatStrategy currentStrategy);
}