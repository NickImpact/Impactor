/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.sponge.scoreboard.objective.types;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.api.scoreboard.components.TimeConfiguration;
import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import net.impactdev.impactor.api.scoreboard.objective.types.RefreshingObjective;
import net.impactdev.impactor.api.services.text.MessageService;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.scoreboard.objective.AbstractSpongeObjective;
import net.impactdev.impactor.sponge.scoreboard.util.SourceResolvers;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

import java.util.concurrent.TimeUnit;

public class SpongeRefreshingObjective extends AbstractSpongeObjective implements RefreshingObjective {

    private String raw;
    private TimeConfiguration timings;
    private boolean async;
    private PlaceholderSources sources;

    private SchedulerTask updater;

    private SpongeRefreshingObjective(SpongeRefreshingObjectiveBuilder builder) {
        this.raw = builder.raw;
        this.timings = builder.timings;
        this.async = builder.async;
        this.sources = builder.sources;
    }

    @Override
    public Component getText() {
        MessageService<Component> service = Impactor.getInstance().getRegistry().get(MessageService.class);
        return service.parse(this.raw, sources);
    }

    @Override
    public TimeConfiguration getTimingConfig() {
        return this.timings;
    }

    @Override
    public void start() {
        if(this.async) {
            this.updater = Impactor.getInstance().getScheduler().asyncRepeating(
                    this::update,
                    this.timings.isTickBased() ? this.timings.getInterval() * 50 : this.timings.getInterval(),
                    this.timings.getUnit()
            );
        } else {
            ScheduledTask task;
            if(this.timings.isTickBased()) {
                task = Sponge.server().scheduler().submit(Task.builder()
                        .execute(this::update)
                        .interval(Ticks.of(this.timings.getInterval()))
                        .plugin(SpongeImpactorPlugin.getInstance().getPluginContainer())
                        .build()
                );
            } else {
                task = Sponge.server().scheduler().submit(Task.builder()
                        .execute(this::update)
                        .interval(this.timings.getInterval(), this.timings.getUnit())
                        .plugin(SpongeImpactorPlugin.getInstance().getPluginContainer())
                        .build()
                );
            }
            this.updater = task::cancel;
        }
    }

    @Override
    public void update() {
        this.getDelegate().setDisplayName(this.getText());
    }

    @Override
    public void shutdown() {
        this.updater.cancel();
    }

    @Override
    public void consumeFocus(ServerPlayer focus) {
        this.sources = PlaceholderSources.builder()
                .from(this.sources)
                .appendIfAbsent(ServerPlayer.class, SourceResolvers.PLAYER.apply(focus.uniqueId()))
                .build();
    }

    @Override
    public ScoreboardObjective copy() {
        SpongeRefreshingObjective clone = new SpongeRefreshingObjective(new SpongeRefreshingObjectiveBuilder());
        clone.raw = this.raw;
        clone.sources = this.sources;
        clone.timings = this.timings;
        clone.async = this.async;
        return clone;
    }

    public static class SpongeRefreshingObjectiveBuilder implements RefreshingObjectiveBuilder {

        private String raw;
        private TimeConfiguration timings;
        private boolean async;
        private PlaceholderSources sources = PlaceholderSources.empty();

        @Override
        public SpongeRefreshingObjectiveBuilder text(String raw) {
            this.raw = raw;
            return this;
        }

        @Override
        public SpongeRefreshingObjectiveBuilder rate(long ticks) {
            this.timings = TimeConfiguration.ofTicks(ticks);
            return this;
        }

        @Override
        public SpongeRefreshingObjectiveBuilder rate(long duration, TimeUnit unit) {
            this.timings = TimeConfiguration.of(duration, unit);
            return this;
        }

        @Override
        public SpongeRefreshingObjectiveBuilder async() {
            this.async = true;
            return this;
        }

        @Override
        public RefreshingObjectiveBuilder sources(PlaceholderSources sources) {
            this.sources = sources;
            return this;
        }

        @Override
        public SpongeRefreshingObjectiveBuilder from(RefreshingObjective input) {
            this.raw = ((SpongeRefreshingObjective) input).raw;
            this.timings = input.getTimingConfig();

            return this;
        }

        @Override
        public RefreshingObjective build() {
            return new SpongeRefreshingObjective(this);
        }
    }

}
