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
import net.impactdev.impactor.api.scoreboards.display.resolvers.config.ResolverConfiguration;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.score.Score;
import org.jetbrains.annotations.NotNull;

public final class ImpactorScoreboardLine implements ScoreboardLine {

    private final ResolverConfiguration<?> resolver;
    private final Score score;

    public ImpactorScoreboardLine(ImpactorScoreboardLineBuilder builder) {
        this.resolver = builder.resolver;
        this.score = builder.score;
    }

    @Override
    public ResolverConfiguration<?> resolver() {
        return this.resolver;
    }

    @Override
    public @NotNull Score score() {
        return this.score;
    }

    public ScoreboardLine.Displayed create(AssignedScoreboard scoreboard) {
        return new DisplayedLine(scoreboard, this);
    }

    public static final class ImpactorScoreboardLineBuilder implements LineBuilder {

        private ResolverConfiguration<?> resolver;
        private Score score;

        @Override
        public LineBuilder resolver(ResolverConfiguration<?> resolver) {
            this.resolver = resolver;
            return this;
        }

        @Override
        public LineBuilder score(Score score) {
            this.score = score;
            return this;
        }

        @Override
        public ScoreboardLine build() {
            return new ImpactorScoreboardLine(this);
        }
    }
}
