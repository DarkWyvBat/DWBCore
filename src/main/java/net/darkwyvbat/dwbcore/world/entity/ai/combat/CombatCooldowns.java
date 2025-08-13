package net.darkwyvbat.dwbcore.world.entity.ai.combat;

import net.darkwyvbat.dwbcore.util.Cooldown;

public record CombatCooldowns(Cooldown path, Cooldown melee, Cooldown ranged, Cooldown retreat) {
    public CombatCooldowns() {
        this(new Cooldown(), new Cooldown(), new Cooldown(), new Cooldown());
    }

    public void tick() {
        path.tick();
        melee.tick();
        ranged.tick();
        retreat.tick();
    }

    public void reset() {
        path.reset();
        melee.reset();
        ranged.reset();
        retreat.reset();
    }
}
