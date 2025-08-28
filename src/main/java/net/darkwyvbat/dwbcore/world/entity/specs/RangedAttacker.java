package net.darkwyvbat.dwbcore.world.entity.specs;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

public interface RangedAttacker extends TacticalGearAgent {
    boolean hasRanged();

    boolean readyForRanged();

    void prepareRanged();

    void performRangedAttack(Entity target, InteractionHand hand, float charge);
}