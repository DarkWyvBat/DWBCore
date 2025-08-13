package net.darkwyvbat.dwbcore.world.entity.ai.combat;

public abstract class CombatStrategy {
    public void start(CombatState state) {
    }

    public void stop(CombatState state) {
    }

    public void tick(CombatState state) {
    }

    public abstract boolean canStart(CombatState state);
}