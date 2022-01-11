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

package net.impactdev.impactor.sponge.scoreboard.lines.types.updatable;

import com.google.common.base.Preconditions;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.api.scoreboard.components.LineIdentifier;
import net.impactdev.impactor.api.scoreboard.components.TimeConfiguration;
import net.impactdev.impactor.api.scoreboard.frames.ScoreboardFrame;
import net.impactdev.impactor.api.scoreboard.lines.types.AnimatedLine;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.scoreboard.lines.AbstractSpongeSBLine;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.util.Ticks;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SpongeAnimatedLine extends AbstractSpongeSBLine implements AnimatedLine {

    private final CircularLinkedList<ScoreboardFrame> frames;
    private final TimeConfiguration timing;
    private final int updates;
    private final boolean async;

    private final Team team;
    private final Component identifier;

    private SchedulerTask updater;
    private int counter;

    private SpongeAnimatedLine(SpongeAnimatedBuilder builder) {
        super(builder.score);
        this.frames = builder.frames;
        this.timing = builder.timing;
        this.updates = builder.updates;
        this.async = builder.async;

        this.team = Team.builder().name(UUID.randomUUID().toString().substring(0, 16)).build();
        this.team.addMember(this.identifier = LineIdentifier.generate());
    }

    @Override
    public void setup(Scoreboard scoreboard, Objective objective, ServerPlayer target) {
        objective.findOrCreateScore(this.identifier).setScore(this.getScore());
        scoreboard.registerTeam(this.team);
        this.team.setPrefix(this.getText());

        this.frames.next();
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
                        .plugin(SpongeImpactorPlugin.getInstance().getPluginContainer())
                        .build()
                );
            } else {
                task = Sponge.server().scheduler().submit(Task.builder()
                        .execute(this::update)
                        .interval(this.timing.getInterval(), this.timing.getUnit())
                        .plugin(SpongeImpactorPlugin.getInstance().getPluginContainer())
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
        this.frames.getCurrent()
                .filter(ScoreboardFrame::shouldUpdateOnTick)
                .ifPresent(frame -> this.team.setPrefix(this.getText()));
    }

    @Override
    public void shutdown() {
        this.updater.cancel();
    }

    @Override
    public Component getText() {
        return this.frames.getCurrent()
                .orElseGet(() -> this.frames.next().orElseThrow(() -> new IllegalStateException("Line with no frames loaded")))
                .getText();
    }

    @Override
    public CircularLinkedList<ScoreboardFrame> getFrames() {
        return this.frames;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public TimeConfiguration getTimingConfig() {
        return this.timing;
    }

    @Override
    public int getUpdateAmount() {
        return this.updates;
    }

    public static SpongeAnimatedBuilder builder() {
        return new SpongeAnimatedBuilder();
    }

    public static class SpongeAnimatedBuilder implements AnimatedLine.AnimatedBuilder {

        private final CircularLinkedList<ScoreboardFrame> frames = CircularLinkedList.of();
        private int score;
        private TimeConfiguration timing;
        private int updates;
        private boolean async;

        @Override
        public AnimatedBuilder frame(ScoreboardFrame frame) {
            this.frames.append(frame);
            return this;
        }

        @Override
        public AnimatedBuilder frames(Iterable<ScoreboardFrame> frames) {
            for(ScoreboardFrame frame : frames) {
                this.frames.append(frame);
            }
            return this;
        }

        @Override
        public AnimatedBuilder score(int score) {
            this.score = score;
            return this;
        }

        @Override
        public AnimatedBuilder interval(long ticks) {
            this.timing = TimeConfiguration.ofTicks(ticks);
            return this;
        }

        @Override
        public AnimatedLine.AnimatedBuilder interval(long interval, TimeUnit unit) {
            this.timing = TimeConfiguration.of(interval, unit);
            return this;
        }

        @Override
        public AnimatedBuilder updates(int amount) {
            this.updates = amount;
            return this;
        }

        @Override
        public AnimatedBuilder async() {
            this.async = true;
            return this;
        }

        @Override
        public AnimatedLine.AnimatedBuilder from(AnimatedLine input) {
            return this;
        }

        @Override
        public AnimatedLine build() {
            Preconditions.checkArgument(this.frames.size() > 0);
            Preconditions.checkArgument(this.timing != null);

            return new SpongeAnimatedLine(this);
        }
    }
}
