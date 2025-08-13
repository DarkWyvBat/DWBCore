package net.darkwyvbat.dwbcore.util;

import java.util.random.RandomGenerator;

public final class PoorRandom implements RandomGenerator {

    private static final PoorRandom INSTANCE = new PoorRandom((int) System.nanoTime());

    private int seed;

    public PoorRandom(int seed) {
        this.seed = seed;
    }

    public PoorRandom() {
        this((int) (System.nanoTime() ^ (System.nanoTime() >>> 32)));
    }

    public static float quickFloat() {
        return INSTANCE.nextFloat();
    }

    public static int quickInt() {
        return INSTANCE.nextInt();
    }

    public static boolean quickProb(float chance) {
        return INSTANCE.probability(chance);
    }

    public boolean probability(float chance) {
        return nextFloat() < chance;
    }

    private int nextState() {
        return this.seed = this.seed * 1664525 + 1013904223;
    }

    @Override
    public long nextLong() {
        long h = nextState(), l = nextState();
        return (h << 32) | (l & 0xFFFFFFFFL);
    }

    @Override
    public int nextInt() {
        return nextState();
    }

    @Override
    public float nextFloat() {
        return Float.intBitsToFloat(0x3f800000 | (nextState() >>> 9)) - 1.0F;
    }
}