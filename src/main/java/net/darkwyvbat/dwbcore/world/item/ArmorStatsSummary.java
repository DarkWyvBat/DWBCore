package net.darkwyvbat.dwbcore.world.item;

public record ArmorStatsSummary(double protection, double knockbackResistance) {
    public static final ArmorStatsSummary EMPTY = new ArmorStatsSummary(0.0, 0.0);
}
