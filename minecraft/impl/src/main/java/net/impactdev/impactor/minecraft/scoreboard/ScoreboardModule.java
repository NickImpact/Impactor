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

import net.impactdev.impactor.api.providers.BuilderProvider;
import net.impactdev.impactor.api.providers.FactoryProvider;
import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.Scoreboard;
import net.impactdev.impactor.api.scoreboards.display.formatters.styling.rgb.ColorCycle;
import net.impactdev.impactor.api.scoreboards.display.text.ComponentElement;
import net.impactdev.impactor.api.scoreboards.display.text.ScoreboardComponent;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLineBuilder;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;
import net.impactdev.impactor.api.scoreboards.score.Score;
import net.impactdev.impactor.api.scoreboards.updaters.scheduled.ScheduledConfiguration;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.minecraft.scoreboard.assigned.AssignedScoreboardImpl;
import net.impactdev.impactor.minecraft.scoreboard.display.formatters.ColorCycleFormatter;
import net.impactdev.impactor.minecraft.scoreboard.display.lines.ImpactorScoreboardLine;
import net.impactdev.impactor.minecraft.scoreboard.display.objectives.ImpactorObjective;
import net.impactdev.impactor.minecraft.scoreboard.display.score.ImpactorScore;
import net.impactdev.impactor.minecraft.scoreboard.renderers.PacketBasedRenderer;
import net.impactdev.impactor.minecraft.scoreboard.text.ImpactorComponentElement;
import net.impactdev.impactor.minecraft.scoreboard.text.ImpactorScoreboardComponent;
import net.impactdev.impactor.minecraft.scoreboard.updaters.scheduled.ScheduledConfigurationImpl;
import net.impactdev.impactor.minecraft.scoreboard.updaters.subscribed.SubscribedConfigurationImpl;

public final class ScoreboardModule implements ImpactorModule {

    @Override
    public void factories(FactoryProvider provider) {
        provider.register(ScoreboardRenderer.Factory.class, new ImplementationFactory());
        provider.register(AssignedScoreboard.Factory.class, AssignedScoreboardImpl::new);
        provider.register(ScoreboardComponent.Factory.class, new ImpactorScoreboardComponent.ScoreboardComponentFactory());
        provider.register(ComponentElement.ElementFactory.class, new ImpactorComponentElement.ComponentElementFactory());

        // Updaters
        provider.register(ScheduledConfiguration.ProvideScheduler.class, new ScheduledConfigurationImpl.Configuration());
        provider.register(SubscribedConfigurationImpl.SubscribedConfigFactory.class, new SubscribedConfigurationImpl.SubscribedConfigFactory());
    }

    @Override
    public void builders(BuilderProvider provider) {
        // Scoreboard
        provider.register(Scoreboard.ScoreboardBuilder.class, ImpactorScoreboard.ImpactorScoreboardBuilder::new);
        provider.register(Objective.ObjectiveBuilder.class, ImpactorObjective.ImpactorObjectiveBuilder::new);
        provider.register(ScoreboardLineBuilder.class, ImpactorScoreboardLine.ImpactorScoreboardLineBuilder::new);
        provider.register(Score.ScoreBuilder.class, ImpactorScore.ImpactorScoreBuilder::new);

        // Formatters
        provider.register(ColorCycle.Config.class, ColorCycleFormatter.FormatterConfig::new);
    }

    private static final class ImplementationFactory implements ScoreboardRenderer.Factory {

        @Override
        public ScoreboardRenderer packets() {
            return new PacketBasedRenderer();
        }

    }
}
