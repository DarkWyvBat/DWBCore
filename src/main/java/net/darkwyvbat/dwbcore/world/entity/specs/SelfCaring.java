package net.darkwyvbat.dwbcore.world.entity.specs;

import net.minecraft.world.InteractionHand;

public interface SelfCaring extends TacticalGearAgent {

    boolean hasForCare();

    void prepareForCare(InteractionHand slot);

    void startCaring(InteractionHand hand);

    void stopCaring();

    float getHealthPercent();

    boolean isFullHealth();
}