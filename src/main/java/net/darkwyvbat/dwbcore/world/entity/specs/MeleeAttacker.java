package net.darkwyvbat.dwbcore.world.entity.specs;

public interface MeleeAttacker extends TacticalGearAgent {
    boolean hasMelee();

    boolean readyForMelee();

    void prepareMelee();
}