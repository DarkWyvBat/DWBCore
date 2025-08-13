package net.darkwyvbat.dwbcore.world.entity.ai.combat;

public record MeleeConfig(double speed, double maxDistSqr, int cd) {

    public static MeleeConfig fromRanges(double speed, double maxDist, int cd) {
        return new MeleeConfig(speed, maxDist * maxDist, cd);
    }
}
