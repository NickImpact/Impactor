package net.impactdev.impactor.api.scoreboard.components;

import java.util.concurrent.TimeUnit;

public class TimeConfiguration {

    private final boolean ticks;

    private final long interval;
    private final TimeUnit unit;

    private TimeConfiguration(long ticks) {
        this.ticks = true;
        this.interval = ticks;
        this.unit = TimeUnit.MILLISECONDS;
    }

    private TimeConfiguration(long interval, TimeUnit unit) {
        this.ticks = false;
        this.interval = interval;
        this.unit = unit;
    }

    public static TimeConfiguration ofTicks(long ticks) {
        return new TimeConfiguration(ticks);
    }

    public static TimeConfiguration of(long interval, TimeUnit unit) {
        return new TimeConfiguration(interval, unit);
    }

    public boolean isTickBased() {
        return ticks;
    }

    public long getInterval() {
        return interval;
    }

    public TimeUnit getUnit() {
        return unit;
    }

}
