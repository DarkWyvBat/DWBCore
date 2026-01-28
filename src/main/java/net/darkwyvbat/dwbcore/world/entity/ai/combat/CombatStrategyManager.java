package net.darkwyvbat.dwbcore.world.entity.ai.combat;

import net.darkwyvbat.dwbcore.util.time.TickingCooldown;

import java.util.ArrayList;
import java.util.List;

public class CombatStrategyManager {
    public static final int DEFAULT_CHANGE_INTERVAL = 10;

    private final List<CombatStrategy> strategies;
    private final CombatStrategy defaultStrategy;
    private final TickingCooldown strategyChangeCD = new TickingCooldown();

    private CombatStrategy prevStrategy;
    private CombatStrategy currentStrategy;
    private CombatState state;

    private CombatStrategyManager(List<CombatStrategy> strategies, CombatStrategy defaultStrategy, int changeInterval) {
        this.strategies = List.copyOf(strategies);
        this.defaultStrategy = defaultStrategy;
        this.strategyChangeCD.set(changeInterval);
    }

    public void setState(CombatState state) {
        this.state = state;
    }

    public void tick(CombatState state) {
        if (strategyChangeCD.tick()) changeStrategy();
        currentStrategy.tick(state);
    }

    private void changeStrategy() {
        CombatStrategy newStrategy = strategies.stream().filter(s -> s.canStart(state, currentStrategy)).findFirst().orElse(defaultStrategy);
        if (newStrategy != currentStrategy)
            setStrategy(newStrategy);
    }

    private void setStrategy(CombatStrategy newStrategy) {
        currentStrategy.stop(state, newStrategy);
        prevStrategy = currentStrategy;
        currentStrategy = newStrategy;
        currentStrategy.start(state, prevStrategy);
    }

    public void onStart() {
        prevStrategy = null;
        currentStrategy = defaultStrategy;
        strategyChangeCD.reset();
        state.cooldowns().reset();
        currentStrategy.start(state, prevStrategy);
    }

    public void onStop() {
        currentStrategy.stop(state, null);
        currentStrategy = null;
        prevStrategy = null;
    }

    public CombatState getState() {
        return state;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<CombatStrategy> strategies = new ArrayList<>();
        private CombatStrategy defaultStrategy;
        private int changeInterval = DEFAULT_CHANGE_INTERVAL;

        public CombatStrategyManager build() {
            return new CombatStrategyManager(strategies, defaultStrategy, changeInterval);
        }

        public Builder add(CombatStrategy strategy) {
            strategies.add(strategy);
            return this;
        }

        public Builder defaultStrategy(CombatStrategy strategy) {
            defaultStrategy = strategy;
            return this;
        }

        public Builder changeInterval(int ticks) {
            changeInterval = ticks;
            return this;
        }
    }
}