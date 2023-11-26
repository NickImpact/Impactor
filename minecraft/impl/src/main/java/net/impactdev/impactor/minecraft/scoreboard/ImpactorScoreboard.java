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

package net.impactdev.impactor.minecraft.scoreboard;

import net.impactdev.impactor.api.scoreboards.Scoreboard;
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;

import java.util.ArrayList;
import java.util.List;

public final class ImpactorScoreboard implements Scoreboard {

    private final ScoreboardRenderer implementation;
    private final Objective objective;
    private final List<ScoreboardLine> lines;

    private ImpactorScoreboard(ImpactorScoreboardBuilder builder) {
        this.implementation = builder.implementation;
        this.objective = builder.objective;
        this.lines = builder.lines;
    }

    @Override
    public ScoreboardRenderer implementation() {
        return this.implementation;
    }

    @Override
    public Objective objective() {
        return this.objective;
    }

    @Override
    public List<ScoreboardLine> lines() {
        return this.lines;
    }

    public static class ImpactorScoreboardBuilder implements ScoreboardBuilder {

        private ScoreboardRenderer implementation;
        private Objective objective;
        private final List<ScoreboardLine> lines = new ArrayList<>();

        @Override
        public ScoreboardBuilder implementation(ScoreboardRenderer implementation) {
            this.implementation = implementation;
            return this;
        }

        @Override
        public ScoreboardBuilder objective(Objective objective) {
            this.objective = objective;
            return this;
        }

        @Override
        public ScoreboardBuilder line(ScoreboardLine line) {
            this.lines.add(line);
            return this;
        }

        @Override
        public Scoreboard build() {
            return new ImpactorScoreboard(this);
        }
    }

}
