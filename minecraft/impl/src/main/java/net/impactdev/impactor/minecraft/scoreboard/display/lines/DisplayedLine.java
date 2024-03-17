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
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.score.Score;
import net.impactdev.impactor.minecraft.scoreboard.display.AbstractDisplay;
import net.impactdev.impactor.minecraft.scoreboard.display.score.ImpactorScore;

public class DisplayedLine extends AbstractDisplay implements ScoreboardLine.Displayed {

    private final ImpactorScoreboardLine delegate;
    private final Score.Mutable score;

    public DisplayedLine(AssignedScoreboard scoreboard, ImpactorScoreboardLine delegate) {
        super(scoreboard, delegate);
        this.delegate = delegate;
        this.score = ((ImpactorScore) delegate.score()).asMutable();
    }

    @Override
    public ScoreboardLine delegate() {
        return this.delegate;
    }

    @Override
    public Score.Mutable score() {
        return this.score;
    }

    @Override
    protected void render(AssignedScoreboard scoreboard, ScoreboardRenderer renderer) {
        renderer.line(scoreboard, this);
    }

    @Override
    protected void onTick(AssignedScoreboard scoreboard) {
        this.delegate.lineTickConsumer().ifPresent(consumer -> consumer.onScoreTick(scoreboard.viewer(), this.score));
    }
}
