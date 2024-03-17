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

package net.impactdev.impactor.minecraft.scoreboard.display.lines;

import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.display.text.ScoreboardComponent;
import net.impactdev.impactor.api.scoreboards.lines.LineTickConsumer;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLineBuilder;
import net.impactdev.impactor.api.scoreboards.score.Score;
import net.impactdev.impactor.api.scoreboards.updaters.UpdaterConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class ImpactorScoreboardLine implements ScoreboardLine {

    private final ScoreboardComponent text;
    private final Score score;
    private final UpdaterConfiguration<?> updater;
    private final LineTickConsumer lineTickConsumer;

    public ImpactorScoreboardLine(ImpactorScoreboardLineBuilder builder) {
        this.text = builder.text;
        this.score = builder.score;
        this.updater = builder.updater;

        this.lineTickConsumer = builder.lineTickConsumer;
    }

    @Override
    public @NotNull Score score() {
        return this.score;
    }

    public ScoreboardLine.Displayed create(AssignedScoreboard scoreboard) {
        return new DisplayedLine(scoreboard, this);
    }

    @Override
    public ScoreboardComponent component() {
        return this.text;
    }

    @Override
    public @Nullable UpdaterConfiguration<?> updater() {
        return this.updater;
    }

    Optional<LineTickConsumer> lineTickConsumer() {
        return Optional.ofNullable(this.lineTickConsumer);
    }

    public static final class ImpactorScoreboardLineBuilder implements ScoreboardLineBuilder {

        private ScoreboardComponent text;
        private UpdaterConfiguration<?> updater;
        private Score score;

        private LineTickConsumer lineTickConsumer;

        @Override
        public ScoreboardLineBuilder text(ScoreboardComponent component) {
            this.text = component;
            return this;
        }

        @Override
        public ScoreboardLineBuilder score(Score score) {
            this.score = score;
            return this;
        }

        @Override
        public ScoreboardLineBuilder updater(UpdaterConfiguration<?> config) {
            this.updater = config;
            return this;
        }

        @Override
        public ScoreboardLineBuilder onTickLine(LineTickConsumer resolver) {
            this.lineTickConsumer = resolver;
            return this;
        }

        @Override
        public ScoreboardLine build() {
            return new ImpactorScoreboardLine(this);
        }
    }
}
