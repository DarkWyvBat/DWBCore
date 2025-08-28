package net.darkwyvbat.dwbcore.world.entity.ai.combat;

public record MeleeConfig(double speed, int attackCD) {

    public static MeleeConfig fromRanges(double speed, int attackCD) {
        return new MeleeConfig(speed, attackCD);
    }
}
