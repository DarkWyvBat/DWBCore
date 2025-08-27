package net.darkwyvbat.dwbcore.util.time;

public class TimestampCooldown {
    private long duration;
    private long timestamp;

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
}