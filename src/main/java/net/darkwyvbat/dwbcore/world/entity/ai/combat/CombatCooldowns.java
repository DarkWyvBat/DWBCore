package net.darkwyvbat.dwbcore.world.entity.ai.combat;

import net.darkwyvbat.dwbcore.util.time.TimestampCooldown;

public record CombatCooldowns(TimestampCooldown path, TimestampCooldown melee, TimestampCooldown ranged) {
    public CombatCooldowns() {
        this(new TimestampCooldown(), new TimestampCooldown(), new TimestampCooldown());
    }

    public void reset() {
        path.reset();
        melee.reset();
        ranged.reset();
    }
}
