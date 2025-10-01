package net.darkwyvbat.dwbcore.util.time;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TimestampCooldown {

    public static final Codec<TimestampCooldown> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.LONG.fieldOf("duration").forGetter(TimestampCooldown::getDuration),
            Codec.LONG.fieldOf("timestamp").forGetter(TimestampCooldown::getTimestamp)
    ).apply(i, TimestampCooldown::new));

    protected long duration;
    protected long timestamp;

    public TimestampCooldown() {
        reset();
    }

    public TimestampCooldown(long duration, long currentTime) {
        set(duration, currentTime);
    }

    public void set(long duration, long currentTIme) {
        this.duration = duration;
        timestamp = currentTIme + duration;
        if (timestamp < currentTIme)
            timestamp = Long.MAX_VALUE;
    }

    public void reset() {
        duration = 0;
        timestamp = 0;
    }

    public boolean isReady(long currentTIme) {
        return currentTIme >= timestamp;
    }

    public long getRemainingTime(long currentTIme) {
        return isReady(currentTIme) ? 0 : timestamp - currentTIme;
    }

    public long getDuration() {
        return duration;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}