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

package net.impactdev.impactor.minecraft.scoreboard.display.objectives;

import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.display.text.ScoreboardComponent;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;
import net.impactdev.impactor.api.scoreboards.score.ScoreFormatter;
import net.impactdev.impactor.api.scoreboards.updaters.UpdaterConfiguration;
import org.jetbrains.annotations.Nullable;

public final class ImpactorObjective implements Objective {

    private final ScoreboardComponent text;
    private final ScoreFormatter formatter;
    private final UpdaterConfiguration<?> updater;

    private ImpactorObjective(ImpactorObjectiveBuilder builder) {
        this.text = builder.text;
        this.formatter = builder.formatter;
        this.updater = builder.updater;
    }

    public DisplayedObjective create(AssignedScoreboard scoreboard) {
        return new DisplayedObjective(scoreboard, this);
    }

    @Override
    public ScoreboardComponent text() {
        return this.text;
    }

    @Override
    public @Nullable ScoreFormatter formatter() {
        return this.formatter;
    }

    @Override
    public ScoreboardComponent component() {
        return this.text;
    }

    @Override
    public @Nullable UpdaterConfiguration<?> updater() {
        return this.updater;
    }

    public static final class ImpactorObjectiveBuilder implements ObjectiveBuilder {

        private ScoreboardComponent text;
        private ScoreFormatter formatter;
        private UpdaterConfiguration<?> updater;

        @Override
        public ObjectiveBuilder text(ScoreboardComponent component) {
            this.text = component;
            return this;
        }

        @Override
        public ObjectiveBuilder formatter(ScoreFormatter formatter) {
            this.formatter = formatter;
            return this;
        }

        @Override
        public ObjectiveBuilder updater(UpdaterConfiguration<?> config) {
            this.updater = config;
            return this;
        }

        @Override
        public Objective build() {
            return new ImpactorObjective(this);
        }
    }

}
