package net.darkwyvbat.dwbcore.world.entity.ai.opinion;

import net.darkwyvbat.dwbcore.util.Valued;

public enum InterestLevel implements Valued<Integer> {
    NONE(0),
    LOW(10),
    MEDIUM(20),
    HIGH(50),
    FAN(100);

    private final int value;

    InterestLevel(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
