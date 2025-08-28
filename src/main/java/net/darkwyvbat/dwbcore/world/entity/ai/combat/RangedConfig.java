package net.darkwyvbat.dwbcore.world.entity.ai.combat;

public record RangedConfig(int lostRangeSqr, int startDistSqr, int maxRangeSqr, int prefRangeSqr,
                           int startKitingDistSqr, int cd) {

    public static RangedConfig fromRanges(int lostRange, int startDist, int maxRange, int prefRange, int startKitingDist, int cd) {
        return new RangedConfig(
                lostRange * lostRange,
                startDist * startDist,
                maxRange * maxRange,
                prefRange * prefRange,
                startKitingDist * startKitingDist,
                cd
        );
    }
}