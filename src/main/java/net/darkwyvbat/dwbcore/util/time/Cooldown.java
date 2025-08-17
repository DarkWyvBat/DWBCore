package net.darkwyvbat.dwbcore.util.time;

public class Cooldown {

    protected final int defaultValue;
    protected int ticks;

    public Cooldown() {
        this(0, 0);
    }

    public Cooldown(int defaultValue) {
        this(0, defaultValue);
    }

    public Cooldown(int start, int defaultValue) {
        this.ticks = start;
        this.defaultValue = defaultValue;
    }

    public boolean tick() {
        if (!isReady()) {
            --ticks;
            return false;
        }
        return true;
    }

    public boolean isReady() {
        return ticks <= 0;
    }

    public void set(int v) {
        set(v, isReady());
    }

    public void set(int v, boolean force) {
        if (force) ticks = v;
    }

    public void reset() {
        ticks = defaultValue;
    }

    public int getTicks() {
        return ticks;
    }

    public int getDefaultValue() {
        return defaultValue;
    }
}