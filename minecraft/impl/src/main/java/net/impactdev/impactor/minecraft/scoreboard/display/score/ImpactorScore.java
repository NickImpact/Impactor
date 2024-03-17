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

package net.impactdev.impactor.minecraft.scoreboard.display.score;

import net.impactdev.impactor.api.scoreboards.score.Score;
import net.impactdev.impactor.api.scoreboards.score.ScoreFormatter;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntUnaryOperator;

public final class ImpactorScore implements Score {

    private final ScoreFormatter formatter;

    private final int value;
    private final boolean locked;

    public ImpactorScore(ImpactorScoreBuilder builder) {
        this.value = builder.value;
        this.locked = builder.locked;
        this.formatter = builder.formatter;
    }

    @Override
    public boolean locked() {
        return this.locked;
    }

    @Override
    public int value() {
        return this.value;
    }

    @Override
    public @Nullable ScoreFormatter formatter() {
        return this.formatter;
    }

    public Score.Mutable asMutable() {
        return new ImpactorMutableScore(this.value, this.formatter, this.locked);
    }

    public static final class ImpactorMutableScore implements Score.Mutable {

        private final ScoreFormatter formatter;
        private int score;
        private boolean locked;

        public ImpactorMutableScore(int score, ScoreFormatter formatter, boolean locked) {
            this.score = score;
            this.formatter = formatter;
            this.locked = locked;
        }

        @Override
        public boolean locked() {
            return this.locked;
        }

        @Override
        public void lock() {
            this.locked = true;
        }

        @Override
        public void unlock() {
            this.locked = false;
        }

        @Override
        public int value() {
            return this.score;
        }

        @Override
        public @Nullable ScoreFormatter formatter() {
            return this.formatter;
        }

        @Override
        public boolean update(IntUnaryOperator operator) {
            if(this.locked) {
                return false;
            }

            this.score = operator.applyAsInt(this.score);
            return true;
        }

        @Override
        public boolean set(int value) {
            if(this.locked) {
                return false;
            }

            this.score = value;
            return true;
        }
    }

    public static final class ImpactorScoreBuilder implements ScoreBuilder {

        private int value;
        private boolean locked;

        private ScoreFormatter formatter;

        @Override
        public ScoreBuilder score(int score) {
            this.value = score;
            return this;
        }

        @Override
        public ScoreBuilder formatter(ScoreFormatter formatter) {
            this.formatter = formatter;
            return this;
        }

        @Override
        public ScoreBuilder locked(boolean state) {
            this.locked = state;
            return this;
        }

        @Override
        public Score build() {
            return new ImpactorScore(this);
        }
    }
}
