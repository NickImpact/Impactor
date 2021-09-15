/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2021 NickImpact
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

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.registry.Registry;
import net.impactdev.impactor.api.scoreboard.ImpactorScoreboard;
import net.impactdev.impactor.api.scoreboard.frames.types.ConstantFrame;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.scoreboard.frames.types.RefreshingFrame;
import net.impactdev.impactor.api.scoreboard.lines.types.AnimatedLine;
import net.impactdev.impactor.api.scoreboard.lines.types.ListeningLine;
import net.impactdev.impactor.api.scoreboard.lines.types.RefreshingLine;
import net.impactdev.impactor.api.scoreboard.lines.types.ConstantLine;
import net.impactdev.impactor.api.scoreboard.objective.types.AnimatedObjective;
import net.impactdev.impactor.api.scoreboard.objective.types.ConstantObjective;
import net.impactdev.impactor.api.scoreboard.objective.types.ListeningObjective;
import net.impactdev.impactor.api.scoreboard.objective.types.RefreshingObjective;
import net.impactdev.impactor.sponge.scoreboard.frames.SpongeConstantFrame;
import net.impactdev.impactor.sponge.scoreboard.frames.SpongeListeningFrame;
import net.impactdev.impactor.sponge.scoreboard.frames.SpongeRefreshingFrame;
import net.impactdev.impactor.sponge.scoreboard.lines.types.SpongeConstantLine;
import net.impactdev.impactor.sponge.scoreboard.lines.types.updatable.SpongeAnimatedLine;
import net.impactdev.impactor.sponge.scoreboard.lines.types.updatable.SpongeListeningLine;
import net.impactdev.impactor.sponge.scoreboard.lines.types.updatable.SpongeRefreshingLine;
import net.impactdev.impactor.sponge.scoreboard.objective.types.SpongeAnimatedObjective;
import net.impactdev.impactor.sponge.scoreboard.objective.types.SpongeConstantObjective;
import net.impactdev.impactor.sponge.scoreboard.objective.types.SpongeListeningObjective;
import net.impactdev.impactor.sponge.scoreboard.objective.types.SpongeRefreshingObjective;

public class ScoreboardModule {

    public void initialize() {
        Registry registry = Impactor.getInstance().getRegistry();

        // General Scoreboard
        registry.registerBuilderSupplier(ImpactorScoreboard.ScoreboardBuilder.class, SpongeScoreboard.SpongeScoreboardBuilder::new);

        // Objectives
        registry.registerBuilderSupplier(ConstantObjective.ConstantObjectiveBuilder.class, SpongeConstantObjective.SpongeConstantObjectiveBuilder::new);
        registry.registerBuilderSupplier(RefreshingObjective.RefreshingObjectiveBuilder.class, SpongeRefreshingObjective.SpongeRefreshingObjectiveBuilder::new);
        registry.registerBuilderSupplier(ListeningObjective.ListeningObjectiveBuilder.class, SpongeListeningObjective.SpongeListeningObjectiveBuilder::new);
        registry.registerBuilderSupplier(AnimatedObjective.AnimatedObjectiveBuilder.class, SpongeAnimatedObjective.SpongeAnimatedObjectiveBuilder::new);

        // Lines
        registry.registerBuilderSupplier(ConstantLine.ConstantLineBuilder.class, SpongeConstantLine.SpongeConstantLineBuilder::new);
        registry.registerBuilderSupplier(RefreshingLine.RefreshingLineBuilder.class, SpongeRefreshingLine.SpongeRefreshingLineBuilder::new);
        registry.registerBuilderSupplier(AnimatedLine.AnimatedBuilder.class, SpongeAnimatedLine.SpongeAnimatedBuilder::new);
        registry.registerBuilderSupplier(ListeningLine.ListeningBuilder.class, SpongeListeningLine.SpongeListeningLineBuilder::new);

        // Frames
        registry.registerBuilderSupplier(ConstantFrame.ConstantFrameBuilder.class, SpongeConstantFrame.SpongeConstantFrameBuilder::new);
        registry.registerBuilderSupplier(RefreshingFrame.RefreshingFrameBuilder.class, SpongeRefreshingFrame.SpongeRefreshingFrameBuilder::new);
        registry.registerBuilderSupplier(ListeningFrame.ListeningFrameBuilder.class, SpongeListeningFrame.SpongeListeningFrameBuilder::new);
    }

}
