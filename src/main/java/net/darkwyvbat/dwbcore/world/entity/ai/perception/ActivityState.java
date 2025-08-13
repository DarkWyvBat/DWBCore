package net.darkwyvbat.dwbcore.world.entity.ai.perception;

import net.darkwyvbat.dwbcore.util.Valued;

//TODO
public enum ActivityState implements Valued<Integer> {
    CHILL(0),
    REGULAR(20),
    ACTIVE(40),
    ALERT(60),
    PANIC(80);

    private final int value;

    ActivityState(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    public static ActivityState from(int activityLevel) {
        for (int i = values().length - 1; i >= 0; --i) {
            ActivityState state = values()[i];
            if (activityLevel >= state.getValue())
                return state;
        }
        return CHILL;
    }
}