package net.darkwyvbat.dwbcore.util.time;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TickingCooldown {

    public static final Codec<TickingCooldown> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.LONG.fieldOf("ticks").forGetter(TickingCooldown::getTicks),
            Codec.LONG.fieldOf("default_value").forGetter(TickingCooldown::getDefaultValue)
    ).apply(i, TickingCooldown::new));

    protected long defaultValue;
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
        ticks = v;
    }

    public void reset() {
        ticks = defaultValue;
    }

    public void add(long v) {
        set(getTicks() + v);
    }

    public long getTicks() {
        return ticks;
    }

    public void setDefaultValue(long v) {
        defaultValue = v;
    }

    public long getDefaultValue() {
        return defaultValue;
    }
}