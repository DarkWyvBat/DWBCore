package net.darkwyvbat.dwbcore.world.entity;

public enum MobStates {
    STANDING(0),
    CROUCHING(1),
    SITTING(2),
    SWIMMING(3),
    SLEEPING(4);

    private final int value;

    MobStates(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MobStates fromInt(int value) {
        for (MobStates state : MobStates.values())
            if (state.getValue() == value)
                return state;
        return STANDING;
    }
}
