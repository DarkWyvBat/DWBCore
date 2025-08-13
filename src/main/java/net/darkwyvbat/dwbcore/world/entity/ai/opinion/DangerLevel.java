package net.darkwyvbat.dwbcore.world.entity.ai.opinion;

import net.darkwyvbat.dwbcore.util.Valued;

public enum DangerLevel implements Valued<Integer> {
    DEADLY(-100),
    VERY_DANGEROUS(-50),
    DANGEROUS(-20),
    ANNOYING(-10),
    HARMLESS(0),
    ALLY(10),
    HELPER(100);

    private final int value;

    DangerLevel(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}