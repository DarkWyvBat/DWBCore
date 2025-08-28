package net.darkwyvbat.dwbcore.world.entity.ai.combat;

public class DwbCombatConfigs {
    public static final MeleeConfig HUMANOID_BASE_MELEE = MeleeConfig.fromRanges(1.2, 20);
    public static final RangedConfig HUMANOID_BASE_RANGED = RangedConfig.fromRanges(32, 5, 24, 16, 10, 20);
    public static final CombatConfig HUMANOID_BASE_CONFIG = new CombatConfig(
            1.3,
            -40,
            30,
            HUMANOID_BASE_MELEE,
            HUMANOID_BASE_RANGED
    );
}
