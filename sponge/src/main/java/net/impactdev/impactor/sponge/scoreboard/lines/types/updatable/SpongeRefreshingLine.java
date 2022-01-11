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

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.api.scoreboard.components.LineIdentifier;
import net.impactdev.impactor.api.scoreboard.components.TimeConfiguration;
import net.impactdev.impactor.api.scoreboard.effects.FrameEffect;
import net.impactdev.impactor.api.scoreboard.lines.types.RefreshingLine;
import net.impactdev.impactor.api.services.text.MessageService;
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SpongeRefreshingLine extends AbstractSpongeSBLine implements RefreshingLine {

    private final String raw;
    private final TimeConfiguration timings;

    private final boolean async;

    private final Team team;
    private final Component identifier;

    private final Queue<FrameEffect> effects;
    private final PlaceholderSources sources;

    private SchedulerTask updater;

    public SpongeRefreshingLine(SpongeRefreshingLineBuilder builder) {
        super(builder.score);
        this.raw = builder.raw;
        this.timings = builder.timings;
        this.async = builder.async;
        this.effects = new LinkedList<>(Arrays.asList(builder.effects));
        this.sources = builder.sources;

        this.team = Team.builder().name(UUID.randomUUID().toString().substring(0, 16)).build();
        this.team.addMember(this.identifier = LineIdentifier.generate());
    }

    @Override
    public Component getText() {
        MessageService<Component> service = Impactor.getInstance().getRegistry().get(MessageService.class);
        Component result = service.parse(this.raw, this.sources);
        for(FrameEffect effect : this.effects) {
            result = effect.translate(result);
        }

        return result;
    }

    @Override
    public TimeConfiguration getTimingConfig() {
        return this.timings;
    }

    @Override
    public void setup(Scoreboard scoreboard, Objective objective, ServerPlayer target) {
        objective.findOrCreateScore(this.identifier).setScore(this.getScore());
        scoreboard.registerTeam(this.team);
        this.team.setPrefix(this.getText());
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
        this.team.setPrefix(this.getText());
    }

    @Override
    public void shutdown() {
        this.updater.cancel();
    }

    public static class SpongeRefreshingLineBuilder implements RefreshingLineBuilder {

        private int score;
        private String raw;
        private FrameEffect[] effects = new FrameEffect[0];
        private TimeConfiguration timings;
        private boolean async;
        private PlaceholderSources sources;

        @Override
        public RefreshingLineBuilder score(int score) {
            this.score = score;
            return this;
        }

        @Override
        public RefreshingLineBuilder text(String raw) {
            this.raw = raw;
            return this;
        }

        @Override
        public RefreshingLineBuilder effects(FrameEffect... effects) {
            this.effects = effects;
            return this;
        }

        @Override
        public RefreshingLineBuilder rate(long ticks) {
            this.timings = TimeConfiguration.ofTicks(ticks);
            return this;
        }

        @Override
        public RefreshingLineBuilder rate(long duration, TimeUnit unit) {
            this.timings = TimeConfiguration.of(duration, unit);
            return this;
        }

        @Override
        public RefreshingLineBuilder async() {
            this.async = true;
            return this;
        }

        @Override
        public RefreshingLineBuilder sources(PlaceholderSources sources) {
            this.sources = sources;
            return this;
        }

        @Override
        public RefreshingLineBuilder from(RefreshingLine input) {
            this.raw = ((SpongeRefreshingLine) input).raw;
            this.timings = input.getTimingConfig();

            return this;
        }

        @Override
        public RefreshingLine build() {
            return new SpongeRefreshingLine(this);
        }
    }
}
