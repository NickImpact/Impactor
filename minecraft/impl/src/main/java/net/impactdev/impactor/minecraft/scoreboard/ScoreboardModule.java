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

import net.impactdev.impactor.api.events.ImpactorEvent;
import net.impactdev.impactor.api.platform.players.events.ClientConnectionEvent;
import net.impactdev.impactor.api.providers.BuilderProvider;
import net.impactdev.impactor.api.providers.FactoryProvider;
import net.impactdev.impactor.api.scheduler.Ticks;
import net.impactdev.impactor.api.scheduler.v2.Scheduler;
import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.Scoreboard;
import net.impactdev.impactor.api.scoreboards.display.formatters.styling.rgb.ColorCycle;
import net.impactdev.impactor.api.scoreboards.display.text.ComponentElement;
import net.impactdev.impactor.api.scoreboards.display.text.ScoreboardComponent;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLineBuilder;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;
import net.impactdev.impactor.api.scoreboards.score.Score;
import net.impactdev.impactor.api.scoreboards.updaters.scheduled.ScheduledConfiguration;
import net.impactdev.impactor.api.scoreboards.updaters.scheduled.ScheduledUpdater;
import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.event.EventBus;

import java.util.concurrent.atomic.AtomicBoolean;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;

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

    @Override
    public void subscribe(EventBus<ImpactorEvent> bus) {
        bus.subscribe(ClientConnectionEvent.Join.class, event -> {
            BaseImpactorPlugin.instance().logger().info("Connection established, attempting to send scoreboard...");

            Objective objective = Objective.builder()
                    .text(ScoreboardComponent.create(text("»").color(NamedTextColor.GRAY).appendSpace())
                            .append(ComponentElement.create(
                                    ColorCycle.configure().frames(90).increment(3).build(),
                                            viewer -> text("Impactor Scoreboard Testing")
                            ))
                            .append(space().append(text("«").color(NamedTextColor.GRAY)))
                    )
                    .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                    .build();

            ScoreboardComponent tpsComponent = ScoreboardComponent.create(text("TPS: ").color(NamedTextColor.GRAY))
                    .append(ComponentElement.create(
                            ColorCycle.configure().frames(90).increment(3).build(),
                            viewer -> TextProcessor.mini().parse("<impactor:tps>")
                    ));

            ScoreboardLine tps = ScoreboardLine.builder()
                    .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                    .text(tpsComponent)
                    .score(Score.of(3))
                    .build();

            // TODO - Consider this redesign, especially in context of the resolver configuration
            ScoreboardLine dummy = ScoreboardLine.builder()
                    .text(tpsComponent)
                    .score(Score.of(30))
                    .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                    .onTickLine((viewer, score) -> {
                        AtomicBoolean reverse = new AtomicBoolean(false);
                        score.update(current -> {
                            if(current == 100) {
                                reverse.set(true);
                            } else if(current == 30) {
                                reverse.set(false);
                            }

                            return current + (reverse.get() ? -1 : 1);
                        });
                    })
                    .build();

            ScoreboardComponent msptComponent = ScoreboardComponent.create(text("MSPT: ").color(NamedTextColor.GRAY))
                    .append(ComponentElement.create(
                            ColorCycle.configure().frames(90).increment(3).build(),
                            viewer -> TextProcessor.mini().parse("<impactor:mspt>")
                    ));

            ScoreboardLine mspt = ScoreboardLine.builder()
                    .text(msptComponent)
                    .score(Score.of(2))
                    .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                    .build();

            Scoreboard scoreboard = Scoreboard.builder()
                    .renderer(ScoreboardRenderer.packets())
                    .objective(objective)
                    .line(ScoreboardLine.builder()
                            .score(Score.of(15))
                            .text(ScoreboardComponent.create(text("Player Details:")
                                    .color(NamedTextColor.WHITE)
                                    .decorate(TextDecoration.BOLD)
                            ))
                            .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                            .build()
                    )
                    .line(ScoreboardLine.builder()
                            .score(Score.of(14))
                            .text(ScoreboardComponent.create(ComponentElement.create(
                                    viewer -> TextProcessor.mini().parse(viewer, "<gray>Name: <yellow><impactor:name>")
                            )))
                            .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                            .build()
                    )
                    .line(ScoreboardLine.builder()
                            .score(Score.of(4))
                            .text(ScoreboardComponent.create(empty()))
                            .build()
                    )
                    .line(tps)
                    .line(mspt)
                    .line(ScoreboardLine.builder()
                            .score(Score.of(1))
                            .text(ScoreboardComponent.create(text("Memory Usage: ").color(NamedTextColor.GRAY))
                                    .append(ComponentElement.create(viewer -> TextProcessor.mini().parse("<yellow><impactor:memory_used><gray>/<yellow><impactor:memory_total> MB")))
                            )
                            .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                            .build()
                    )
                    .build();

            AssignedScoreboard assigned = scoreboard.assignTo(event.player());
            assigned.open();
        });
    }

    private static final class ImplementationFactory implements ScoreboardRenderer.Factory {

        @Override
        public ScoreboardRenderer packets() {
            return new PacketBasedRenderer();
        }

    }
}
