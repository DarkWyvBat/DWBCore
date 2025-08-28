package net.darkwyvbat.dwbcore.util.time;

public class TickingCooldown {

    protected final long defaultValue;
    protected long ticks;

    public TickingCooldown() {
        this(0, 0);
    }

    public TickingCooldown(long defaultValue) {
        this(0, defaultValue);
    }

    public TickingCooldown(long start, long defaultValue) {
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

    public void set(long v) {
        set(v, isReady());
    }

    public void set(long v, boolean force) {
        if (force) ticks = v;
    }

    public void reset() {
        ticks = defaultValue;
    }

    public long getTicks() {
        return ticks;
    }

    public long getDefaultValue() {
        return defaultValue;
    }
}