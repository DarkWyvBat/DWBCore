package net.darkwyvbat.dwbcore.world.entity;

import net.minecraft.world.entity.Mob;

public interface GrowableMob<T extends Mob & GrowableMob<T>> extends ChronologicalEntity<T> {

    int getGrowthDuration();

    default void onGrow() {
    }

    @Override
    default void defineTimeline() {
        getTimeline().addEvent(getGrowthDuration(), e -> {
            e.setBaby(false);
            e.onGrow();
        });
    }

}
