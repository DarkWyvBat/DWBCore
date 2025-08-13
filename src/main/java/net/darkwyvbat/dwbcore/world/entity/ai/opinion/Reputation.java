package net.darkwyvbat.dwbcore.world.entity.ai.opinion;

import net.darkwyvbat.dwbcore.util.Valued;

public enum Reputation implements Valued<Integer> {
    ENEMY(-100),
    HATED(-50),
    DISLIKED(-20),
    NEUTRAL(0),
    LIKED(20),
    TRUSTED(50),
    FRIEND(100);

    private final int value;

    Reputation(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}