package net.darkwyvbat.dwbcore.world.entity;

import net.minecraft.world.entity.EntityDimensions;

public interface Crouchable {
    boolean canCrouch();

    EntityDimensions getCrouchDimension();

    void setCrouch(boolean crouching);

    boolean isCrouching();
}
