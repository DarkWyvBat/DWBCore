package net.darkwyvbat.dwbcore.world.entity.ai.combat;

public record RangedConfig(int lostRangeSqr, int maxRangeSqr, int prefRangeSqr, int startKitingDistSqr, int cd) {

    public static RangedConfig fromRanges(int lostRange, int maxRange, int prefRange, int startKitingDist, int cd) {
        return new RangedConfig(
                lostRange * lostRange,
                maxRange * maxRange,
                prefRange * prefRange,
                startKitingDist * startKitingDist,
                cd
        );
    }
}