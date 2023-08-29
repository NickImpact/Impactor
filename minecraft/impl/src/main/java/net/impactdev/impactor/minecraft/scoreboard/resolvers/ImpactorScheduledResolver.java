package net.impactdev.impactor.minecraft.scoreboard.resolvers;

import net.impactdev.impactor.api.scheduler.Scheduler;
import net.impactdev.impactor.api.scheduler.Schedulers;
import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.api.text.transforming.transformers.TextTransformer;
import net.impactdev.impactor.scoreboards.updaters.scheduled.ScheduledResolver;
import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class ImpactorScheduledResolver implements ScheduledResolver {

    private final Supplier<Component> provider;
    private final TextTransformer transformer;

    private final AtomicReference<Component> component;
    private final SchedulerTask task;

    private final Duration duration;
    private final boolean async;

    private ImpactorScheduledResolver(ImpactorScheduledResolverBuilder builder) {
        this.provider = builder.provider;
        this.transformer = builder.transformer;
        this.duration = Duration.of(builder.time, builder.unit.toChronoUnit());
        this.async = builder.async;

        this.component = new AtomicReference<>(Component.empty());
        this.task = this.configure(builder);
    }

    @Override
    public Component resolve() {
        return this.component.updateAndGet(ignore -> this.transformer.transform(this.provider.get()));
    }

    @Override
    public void shutdown() {
        this.task.cancel();
    }

    @Override
    public Duration interval() {
        return this.duration;
    }

    @Override
    public boolean async() {
        return this.async;
    }

    private SchedulerTask configure(ImpactorScheduledResolverBuilder builder) {
        Runnable action = () -> {

        };

        if(builder.async) {
            Scheduler scheduler = Schedulers.require(Scheduler.ASYNCHRONOUS);
            return scheduler.repeating(action, builder.time, builder.unit);
        }

        Scheduler scheduler = Schedulers.require(Scheduler.SYNCHRONOUS);
        return scheduler.repeating(action, builder.time, builder.unit);
    }

    public static final class ImpactorScheduledResolverBuilder implements ScheduledResolverBuilder {

        private Supplier<Component> provider;
        private long time;
        private TimeUnit unit;
        private boolean async;
        private TextTransformer transformer;

        @Override
        public ScheduledResolverBuilder text(Supplier<Component> provider) {
            this.provider = provider;
            return this;
        }

        @Override
        public ScheduledResolverBuilder interval(long time, TimeUnit unit) {
            this.time = time;
            this.unit = unit;
            return this;
        }

        @Override
        public ScheduledResolverBuilder async() {
            this.async = true;
            return this;
        }

        @Override
        public ScheduledResolverBuilder transformer(TextTransformer transformer) {
            this.transformer = transformer;
            return this;
        }

        @Override
        public ScheduledResolver build() {
            return new ImpactorScheduledResolver(this);
        }

    }

}
