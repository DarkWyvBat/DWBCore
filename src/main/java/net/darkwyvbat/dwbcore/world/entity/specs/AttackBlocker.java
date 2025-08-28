package net.darkwyvbat.dwbcore.world.entity.specs;

public interface AttackBlocker extends TacticalGearAgent {
    boolean hasAttackBlocker();

    boolean readyForBlockAttack();

    void prepareForAttackBlocking();

    void startBlockAttack();

    void stopBlockAttack();
}