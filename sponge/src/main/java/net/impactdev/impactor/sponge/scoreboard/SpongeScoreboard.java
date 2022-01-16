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

package net.impactdev.impactor.sponge.scoreboard;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.scoreboard.ImpactorScoreboard;
import net.impactdev.impactor.api.scoreboard.components.ScoreboardComponent;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import net.impactdev.impactor.api.utilities.mappings.Tuple;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.scoreboard.lines.AbstractSpongeSBLine;
import net.impactdev.impactor.sponge.scoreboard.objective.AbstractSpongeObjective;
import net.kyori.adventure.util.TriState;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SpongeScoreboard implements ImpactorScoreboard<ServerPlayer> {

    private final AbstractSpongeObjective objective;
    private final List<ScoreboardLine> lines;

    private final UUID source;
    private Scoreboard delegate;
    private TriState visibility;

    public SpongeScoreboard(SpongeScoreboardBuilder builder) {
        this.objective = (AbstractSpongeObjective) builder.objective;
        this.lines = builder.lines;
        this.delegate = null;
        this.source = null;
        this.visibility = TriState.NOT_SET;
    }

    private SpongeScoreboard(SpongeScoreboardBuilder builder, @NonNull UUID source) {
        Preconditions.checkNotNull(source);

        this.objective = (AbstractSpongeObjective) builder.objective;
        this.lines = builder.lines;
        this.source = source;
        this.visibility = TriState.FALSE;
        this.objective.consumeFocus(this.player().get());
        this.create();
    }

    private Supplier<ServerPlayer> player() {
        return () -> Sponge.server().player(this.source).orElseThrow();
    }

    @Override
    public ScoreboardObjective getTitle() {
        return this.objective;
    }

    @Override
    public List<ScoreboardLine> getLines() {
        return this.lines;
    }

    @Override
    public ImpactorScoreboard<ServerPlayer> assignTo(ServerPlayer user) {
        return new SpongeScoreboardBuilder().from(this).build(user);
    }

    @Override
    public boolean show() {
        Preconditions.checkNotNull(this.source);
        Preconditions.checkNotNull(this.delegate);
        if(this.visibility == TriState.FALSE) {
            this.player().get().setScoreboard(this.delegate);
            this.start();
            this.visibility = TriState.TRUE;
            return true;
        }
        return false;
    }

    @Override
    public boolean hide() {
        Preconditions.checkNotNull(this.source);
        Preconditions.checkNotNull(this.delegate);
        if(this.visibility == TriState.TRUE) {
            this.player().get().setScoreboard(Sponge.server().serverScoreboard().get());
            this.shutdown();
            this.visibility = TriState.FALSE;
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        if(this.objective instanceof Updatable) {
            ((Updatable) this.objective).start();
        }

        for(ScoreboardLine line : this.lines) {
            if(line instanceof Updatable) {
                ((Updatable) line).start();
            }
        }
    }

    @Override
    public void shutdown() {
        if(this.objective instanceof Updatable) {
            ((Updatable) this.objective).shutdown();
        }

        for(ScoreboardLine line : this.lines) {
            if(line instanceof Updatable) {
                ((Updatable) line).shutdown();
            }
        }
    }

    public void create() {
        Preconditions.checkNotNull(this.source);

        this.delegate = Scoreboard.builder().build();
        Objective objective = this.objective.resolve();
        this.delegate.addObjective(objective);
        this.delegate.updateDisplaySlot(objective, DisplaySlots.SIDEBAR);

        this.lines.stream().map(l -> (AbstractSpongeSBLine) l).forEach(l -> l.setup(this.delegate, this.player().get()));
    }

    public static SpongeScoreboardBuilder builder() {
        return new SpongeScoreboardBuilder();
    }

    public static class SpongeScoreboardBuilder implements ScoreboardBuilder<ServerPlayer> {

        private ScoreboardObjective objective;
        private List<ScoreboardLine> lines = Lists.newArrayList();

        @Override
        public LinesComponentBuilder<ServerPlayer> objective(ScoreboardObjective objective) {
            Preconditions.checkNotNull(objective);
            Preconditions.checkArgument(objective instanceof AbstractSpongeObjective);
            this.objective = objective;
            return new SpongeLineComponentBuilder(this);
        }

        @Override
        public SpongeScoreboardBuilder from(ImpactorScoreboard<ServerPlayer> input) {
            this.objective = input.getTitle().copy();
            this.lines = input.getLines().stream()
                    .map(line -> new Tuple<>(line, line.copy()))
                    .map(pair -> ((AbstractSpongeSBLine) pair.getSecond()).assignScore(((AbstractSpongeObjective) this.objective).resolve(), pair.getFirst().getScore()))
                    .toList();
            return this;
        }

        private SpongeScoreboard build(ServerPlayer delegate) {
            Preconditions.checkNotNull(this.objective);
            Preconditions.checkNotNull(delegate);

            return new SpongeScoreboard(this, delegate.uniqueId());
        }

        @Override
        public SpongeScoreboard build() {
            Preconditions.checkNotNull(this.objective);
            return new SpongeScoreboard(this);
        }
    }

    public static class SpongeLineComponentBuilder implements ImpactorScoreboard.LinesComponentBuilder<ServerPlayer> {

        private final SpongeScoreboardBuilder parent;
        private final List<ScoreboardLine> lines = Lists.newArrayList();

        public SpongeLineComponentBuilder(SpongeScoreboardBuilder parent) {
            this.parent = parent;
        }

        @Override
        public LinesComponentBuilder<ServerPlayer> line(ScoreboardLine line, int score) {
            this.lines.add(((AbstractSpongeSBLine) line).assignScore(((AbstractSpongeObjective)this.parent.objective).resolve(), score));
            return this;
        }

        @Override
        public LinesComponentBuilder<ServerPlayer> lines(Map<ScoreboardLine, Integer> lines) {
            lines.forEach(this::line);
            return this;
        }

        @Override
        public ScoreboardBuilder<ServerPlayer> complete() {
            this.parent.lines = this.lines;
            return this.parent;
        }
    }

}
