package net.impactdev.impactor.api.platform.metrics.performance.spark;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.types.DoubleStatistic;
import net.impactdev.impactor.api.platform.metrics.performance.TickDetails;

public final class SparkService implements TickDetails {

    private final Spark spark = SparkProvider.get();

    @Override
    public double ticksPerSecond() {
        DoubleStatistic<StatisticWindow.TicksPerSecond> tps = this.spark.tps();
        return tps.poll(StatisticWindow.TicksPerSecond.SECONDS_10);
    }

    @Override
    public double mspt() {
        return 0;
    }
}
