package net.darkwyvbat.dwbcore.util.time;

public class TickingCooldown {

    protected final int defaultValue;
    protected int ticks;

    public TickingCooldown() {
        this(0, 0);
    }

    public TickingCooldown(int defaultValue) {
        this(0, defaultValue);
    }

    public TickingCooldown(int start, int defaultValue) {
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