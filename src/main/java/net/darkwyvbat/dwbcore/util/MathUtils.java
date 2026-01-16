package net.darkwyvbat.dwbcore.util;

public class MathUtils {
    public static double poorSin(double x) {
        x = ((x % 360) + 360) % 360;
        return x > 180 ? -((x - 180) * (180 - (x - 180)) / 8100.0) : x * (180 - x) / 8100.0;
    }

    public static double poorCos(double x) {
        return poorSin(x + 90);
    }

    public static boolean eqWithin(double a, double b, double e) {
        return Math.abs(a - b) < e;
    }

    public static boolean isBtwn(double v, double l, double h) {
        return v > l && v < h;
    }

    public static boolean isBtwnIncl(double v, double l, double h) {
        return v >= l && v <= h;
    }
}