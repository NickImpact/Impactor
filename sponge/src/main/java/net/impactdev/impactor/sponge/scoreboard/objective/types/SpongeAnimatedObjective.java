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
import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.api.scoreboard.components.ScoreboardComponent;
import net.impactdev.impactor.api.scoreboard.components.TimeConfiguration;
import net.impactdev.impactor.api.scoreboard.frames.ScoreboardFrame;
import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import net.impactdev.impactor.api.scoreboard.objective.types.AnimatedObjective;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.scoreboard.frames.AbstractSpongeFrame;
import net.impactdev.impactor.sponge.scoreboard.objective.AbstractSpongeObjective;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class SpongeAnimatedObjective extends AbstractSpongeObjective implements AnimatedObjective {

    private CircularLinkedList<ScoreboardFrame> frames;
    private TimeConfiguration timing;
    private int updates;
    private boolean async;

    private SchedulerTask updater;
    private int counter;

    private SpongeAnimatedObjective(SpongeAnimatedObjectiveBuilder builder) {
        this.frames = builder.frames;
        this.timing = builder.timing;
        this.updates = builder.updates;
        this.async = builder.async;
    }

    @Override
    public Component getText() {
        return this.frames.getCurrent()
                .orElse(this.frames.next().orElseThrow(() -> new IllegalStateException("No frame available")))
                .getText();
    }

    @Override
    public CircularLinkedList<ScoreboardFrame> getFrames() {
        return this.frames;
    }

    @Override
    public TimeConfiguration getTimingConfig() {
        return this.timing;
    }

    @Override
    public int getIterationCount() {
        return this.updates;
    }

    @Override
    public void start() {
        if(this.async) {
            this.frames.getCurrent()
                    .filter(frame -> frame instanceof ScoreboardFrame.UpdatableFrame)
                    .map(frame -> (ScoreboardFrame.UpdatableFrame) frame)
                    .ifPresent(frame -> frame.initialize(this));

            this.updater = Impactor.getInstance().getScheduler().asyncRepeating(
                    this::update,
                    this.timing.isTickBased() ? this.timing.getInterval() * 50 : this.timing.getInterval(),
                    this.timing.getUnit()
            );
        } else {
            this.frames.getCurrent()
                    .filter(frame -> frame instanceof ScoreboardFrame.UpdatableFrame)
                    .map(frame -> (ScoreboardFrame.UpdatableFrame) frame)
                    .ifPresent(frame -> frame.initialize(this));

            ScheduledTask task;
            if(this.timing.isTickBased()) {
                task = Sponge.server().scheduler().submit(Task.builder()
                        .execute(this::update)
                        .interval(Ticks.of(this.timing.getInterval()))
                        .plugin(SpongeImpactorPlugin.instance().bootstrapper().container())
                        .build()
                );
            } else {
                task = Sponge.server().scheduler().submit(Task.builder()
                        .execute(this::update)
                        .interval(this.timing.getInterval(), this.timing.getUnit())
                        .plugin(SpongeImpactorPlugin.instance().bootstrapper().container())
                        .build()
                );
            }
            this.updater = task::cancel;
        }
    }

    @Override
    public void update() {
        if(this.frames.size() > 1) {
            if(this.updates > 0 && this.updates == this.counter) {
                this.frames.getCurrent()
                        .filter(frame -> frame instanceof ScoreboardFrame.UpdatableFrame)
                        .map(frame -> (ScoreboardFrame.UpdatableFrame) frame)
                        .ifPresent(ScoreboardFrame.UpdatableFrame::shutdown);
                this.frames.next();
                this.frames.getCurrent()
                        .filter(frame -> frame instanceof ScoreboardFrame.UpdatableFrame)
                        .map(frame -> (ScoreboardFrame.UpdatableFrame) frame)
                        .ifPresent(frame -> frame.initialize(this));
                this.counter = 0;
            }
        }

        this.counter++;
        this.getDelegate().setDisplayName(this.getText());
    }

    @Override
    public void shutdown() {
        this.updater.cancel();
    }

    @Override
    public void consumeFocus(ServerPlayer focus) {
        for(ScoreboardFrame frame : this.frames) {
            ((AbstractSpongeFrame) frame).provideSource(focus.uniqueId());
        }
    }

    @Override
    public ScoreboardObjective copy() {
        SpongeAnimatedObjective clone = new SpongeAnimatedObjective(new SpongeAnimatedObjectiveBuilder());
        clone.frames = CircularLinkedList.fromStream(this.frames.getFramesNonCircular()
                .stream()
                .map(ScoreboardComponent::copy));
        clone.timing = this.timing;
        clone.updates = this.updates;
        clone.async = this.async;
        return clone;
    }

    public static class SpongeAnimatedObjectiveBuilder implements AnimatedObjectiveBuilder {

        private final CircularLinkedList<ScoreboardFrame> frames = CircularLinkedList.of();
        private TimeConfiguration timing;
        private int updates;
        private boolean async;

        @Override
        public SpongeAnimatedObjectiveBuilder frame(ScoreboardFrame frame) {
            this.frames.append(frame);
            return this;
        }

        @Override
        public SpongeAnimatedObjectiveBuilder frames(Collection<ScoreboardFrame> frames) {
            for(ScoreboardFrame frame : frames) {
                this.frames.append(frame);
            }
            return this;
        }

        @Override
        public SpongeAnimatedObjectiveBuilder interval(long ticks) {
            this.timing = TimeConfiguration.ofTicks(ticks);
            return this;
        }

        @Override
        public SpongeAnimatedObjectiveBuilder interval(long interval, TimeUnit unit) {
            this.timing = TimeConfiguration.of(interval, unit);
            return this;
        }

        @Override
        public SpongeAnimatedObjectiveBuilder iterations(int amount) {
            this.updates = amount;
            return this;
        }

        @Override
        public SpongeAnimatedObjectiveBuilder async() {
            this.async = true;
            return this;
        }

        @Override
        public AnimatedObjectiveBuilder from(AnimatedObjective input) {
            return this;
        }

        @Override
        public AnimatedObjective build() {
            return new SpongeAnimatedObjective(this);
        }
    }

}
