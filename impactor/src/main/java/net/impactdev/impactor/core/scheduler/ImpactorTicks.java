package net.impactdev.impactor.core.scheduler;

import net.impactdev.impactor.api.scheduler.Ticks;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

public class ImpactorTicks implements Ticks {

    private static final int TICK_DURATION_MS = 50;
    private static final Duration EFFECTIVE_MINIMUM_DURATION = Duration.ofMillis(TICK_DURATION_MS);

    private static final int MINECRAFT_DAY_TICKS = 24000;
    private static final int MINECRAFT_HOUR_TICKS = MINECRAFT_DAY_TICKS / 24;
    private static final double MINECRAFT_MINUTE_TICKS = MINECRAFT_HOUR_TICKS / 60.0;
    private static final double MINECRAFT_SECOND_TICKS = MINECRAFT_MINUTE_TICKS / 60.0;
    private static final int MINECRAFT_EPOCH_OFFSET = 6000;

    private final long ticks;
    private final Duration effectiveMinimumDuration;

    public ImpactorTicks(final long ticks) {
        this.ticks = ticks;
        this.effectiveMinimumDuration = EFFECTIVE_MINIMUM_DURATION.multipliedBy(this.ticks);
    }

    @Override
    public Duration expectedDuration() {
        return this.effectiveMinimumDuration;
    }

    @Override
    public long ticks() {
        return this.ticks;
    }

    @Override
    public long minecraftSeconds() {
        // We do this to try to ensure we get the most accurate number of seconds we can.
        // We know the hour rate is 1000 ticks, we can get an accurate hour count. This reduces the potential
        // for error.
        //
        // We get the number of in-game seconds this object fulfils, there may be a few in-game milliseconds.
        return 60 * 60 * this.ticks / MINECRAFT_HOUR_TICKS + // 3600 seconds in an hour
                (long) ((this.ticks % MINECRAFT_HOUR_TICKS) / MINECRAFT_SECOND_TICKS);
    }

    @Override
    public Duration minecraftDayTimeDuration() {
        return Duration.of(this.minecraftSeconds(), ChronoUnit.SECONDS);
    }

    public static final class Factory implements Ticks.Factory {

        private final Ticks zero;
        private final Ticks single;
        private final Ticks minecraftHour;
        private final Ticks minecraftDay;

        public Factory() {
            this.zero = new ImpactorTicks(0);
            this.single = new ImpactorTicks(1);
            this.minecraftHour = new ImpactorTicks(MINECRAFT_HOUR_TICKS);
            this.minecraftDay = new ImpactorTicks(MINECRAFT_DAY_TICKS);
        }

        @Override
        public @NotNull Ticks of(final long ticks) {
            if (ticks < 0) {
                throw new IllegalArgumentException("Tick must be greater than 0!");
            }
            return new ImpactorTicks(ticks);
        }

        @Override
        public @NotNull Ticks ofWallClockTime(final long time, final @NotNull TemporalUnit temporalUnit) {
            if (time < 0) {
                throw new IllegalArgumentException("Time must be greater than 0!");
            }
            Objects.requireNonNull(temporalUnit);

            final long target = temporalUnit.getDuration().multipliedBy(time).toMillis();
            return this.of((long) Math.ceil(target / (double) TICK_DURATION_MS));
        }

        @Override
        public @NotNull Ticks ofMinecraftSeconds(final long seconds) {
            if (seconds < 0) {
                throw new IllegalArgumentException("Seconds must be greater than 0!");
            }
            return this.of((long) Math.ceil(seconds * MINECRAFT_SECOND_TICKS));
        }

        @Override
        public @NotNull Ticks ofMinecraftHours(final long hours) {
            if (hours < 0) {
                throw new IllegalArgumentException("Hours must be greater than 0!");
            }
            return this.of(hours * MINECRAFT_HOUR_TICKS);
        }

        @Override
        public Ticks zero() {
            return this.zero;
        }

        @Override
        public Ticks single() {
            return this.single;
        }

        @Override
        public Ticks minecraftHour() {
            return this.minecraftHour;
        }

        @Override
        public Ticks minecraftDay() {
            return this.minecraftDay;
        }

    }
}
