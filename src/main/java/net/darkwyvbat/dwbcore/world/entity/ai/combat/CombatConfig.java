package net.darkwyvbat.dwbcore.world.entity.ai.combat;

public record CombatConfig(double speedModifier, int seeTimeStop, int seeTimeMax, MeleeConfig meleeConfig,
                           RangedConfig rangedConfig) {
}
