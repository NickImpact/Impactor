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
import net.impactdev.impactor.api.scheduler.v2.Schedulers;
import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.Scoreboard;
import net.impactdev.impactor.api.scoreboards.display.formatters.styling.rgb.ColorCycle;
import net.impactdev.impactor.api.scoreboards.display.resolvers.NoOpResolver;
import net.impactdev.impactor.api.scoreboards.display.resolvers.scheduled.ScheduledResolverConfiguration;
import net.impactdev.impactor.api.scoreboards.display.resolvers.subscribing.SubscriptionConfiguration;
import net.impactdev.impactor.api.scoreboards.display.resolvers.text.ComponentElement;
import net.impactdev.impactor.api.scoreboards.display.resolvers.text.ScoreboardComponent;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;
import net.impactdev.impactor.api.scoreboards.score.Score;
import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.core.modules.ImpactorModule;
//import net.impactdev.impactor.minecraft.scoreboard.implementations.PacketImplementation;
//import net.impactdev.impactor.minecraft.scoreboard.viewed.ViewedImpactorScoreboard;
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.minecraft.api.events.EntityMoveEvent;
import net.impactdev.impactor.minecraft.scoreboard.assigned.AssignedScoreboardImpl;
import net.impactdev.impactor.minecraft.scoreboard.display.formatters.ColorCycleFormatter;
import net.impactdev.impactor.minecraft.scoreboard.display.lines.ImpactorScoreboardLine;
import net.impactdev.impactor.minecraft.scoreboard.display.objectives.ImpactorObjective;
import net.impactdev.impactor.minecraft.scoreboard.display.resolvers.scheduled.ScheduledResolverConfigurationImpl;
import net.impactdev.impactor.minecraft.scoreboard.display.resolvers.subscribing.ImpactorSubscriberConfiguration;
import net.impactdev.impactor.minecraft.scoreboard.display.score.ImpactorScore;
import net.impactdev.impactor.minecraft.scoreboard.renderers.PacketBasedRenderer;
import net.impactdev.impactor.minecraft.scoreboard.text.ImpactorComponentElement;
import net.impactdev.impactor.minecraft.scoreboard.text.ImpactorScoreboardComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.event.EventBus;

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
        provider.register(SubscriptionConfiguration.Component.class, new ImpactorSubscriberConfiguration.Factory());
    }

    @Override
    public void builders(BuilderProvider provider) {
        // Scoreboard
        provider.register(Scoreboard.ScoreboardBuilder.class, ImpactorScoreboard.ImpactorScoreboardBuilder::new);
        provider.register(Objective.ObjectiveBuilder.class, ImpactorObjective.ImpactorObjectiveBuilder::new);
        provider.register(ScoreboardLine.LineBuilder.class, ImpactorScoreboardLine.ImpactorScoreboardLineBuilder::new);
        provider.register(Score.ScoreBuilder.class, ImpactorScore.ImpactorScoreBuilder::new);

        // Resolvers
        provider.register(ScheduledResolverConfiguration.Configuration.class, ScheduledResolverConfigurationImpl.TaskBuilder::new);

        // Formatters
        provider.register(ColorCycle.Config.class, ColorCycleFormatter.FormatterConfig::new);
    }

    @Override
    public void subscribe(EventBus<ImpactorEvent> bus) {
        bus.subscribe(ClientConnectionEvent.Join.class, event -> {
            BaseImpactorPlugin.instance().logger().info("Connection established, attempting to send scoreboard...");

            Objective objective = Objective.builder()
                    .resolver(ScheduledResolverConfiguration.builder()
                            .component(ScoreboardComponent.create(ComponentElement.create(
                                    (viewer, context) -> text("»").color(NamedTextColor.GRAY).appendSpace()
                            )).append(ComponentElement.create(
                                    ColorCycle.configure().frames(90).increment(3).build(),
                                    (viewer, context) -> text("Impactor Scoreboard Testing")
                            )).append(ComponentElement.create(
                                    (viewer, context) -> space().append(text("«").color(NamedTextColor.GRAY))
                            )))
                            .scheduler(Schedulers.require(Scheduler.ASYNCHRONOUS))
                            .repeating(Ticks.single())
                            .build()
                    )
                    .build();

            ScoreboardComponent tpsComponent = ScoreboardComponent.create(text("TPS: ").color(NamedTextColor.GRAY))
                    .append(ComponentElement.create(
                            ColorCycle.configure().frames(90).increment(3).build(),
                            (viewer, context) -> TextProcessor.mini().parse("<impactor:tps>")
                    ));

            ScoreboardLine tps = ScoreboardLine.builder()
                    .resolver(ScheduledResolverConfiguration.builder()
                            .component(tpsComponent)
                            .scheduler(Schedulers.require(Scheduler.ASYNCHRONOUS))
                            .repeating(Ticks.single())
                            .build()
                    )
                    .score(Score.builder().score(3).build())
                    .build();

            ScoreboardComponent msptComponent = ScoreboardComponent.create(text("MSPT: ").color(NamedTextColor.GRAY))
                    .append(ComponentElement.create(
                            ColorCycle.configure().frames(90).increment(3).build(),
                            (viewer, context) -> TextProcessor.mini().parse("<impactor:mspt>")
                    ));

            ScoreboardLine mspt = ScoreboardLine.builder()
                    .resolver(ScheduledResolverConfiguration.builder()
                            .component(msptComponent)
                            .scheduler(Schedulers.require(Scheduler.ASYNCHRONOUS))
                            .repeating(Ticks.single())
                            .build()
                    )
                    .score(Score.builder().score(2).build())
                    .build();

            Scoreboard scoreboard = Scoreboard.builder()
                    .renderer(ScoreboardRenderer.packets())
                    .objective(objective)
                    .line(ScoreboardLine.builder()
                            .score(Score.builder().score(15).build())
                            .resolver(NoOpResolver.create(ScoreboardComponent.create(
                                    text("Player Details:").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
                            )))
                            .build()
                    )
                    .line(ScoreboardLine.builder()
                            .score(Score.builder().score(14).build())
                            .resolver(NoOpResolver.create(ScoreboardComponent.create(ComponentElement.create(
                                    (viewer, context) -> TextProcessor.mini().parse(viewer, "<gray>Name: <yellow><impactor:name>")
                            ))))
                            .build()
                    )
                    .line(ScoreboardLine.builder()
                            .score(Score.builder().score(13).build())
                            .resolver(SubscriptionConfiguration.component(
                                    ScoreboardComponent.create(ComponentElement.create(
                                            (viewer, context) -> TextProcessor.mini().parse(viewer, "<gray>Location: <yellow><impactor:position>")
                                    )))
                                    .listenAndFilter(EntityMoveEvent.class, e -> e.entity().getUUID().equals(event.player().uuid()))
                            )
                            .build()
                    )
                    .line(ScoreboardLine.builder()
                            .score(Score.builder().score(4).build())
                            .resolver(NoOpResolver.create(ScoreboardComponent.create(empty())))
                            .build()
                    )
                    .line(tps)
                    .line(mspt)
                    .line(ScoreboardLine.builder()
                            .score(Score.builder().score(1).build())
                            .resolver(ScheduledResolverConfiguration.builder()
                                    .component(ScoreboardComponent.create(
                                            text("Memory Usage: ").color(NamedTextColor.GRAY)
                                    ).append(ComponentElement.create((viewer, context) -> TextProcessor.mini().parse("<yellow><impactor:memory_used><gray>/<yellow><impactor:memory_total> MB"))))
                                    .scheduler(Schedulers.require(Scheduler.ASYNCHRONOUS))
                                    .repeating(Ticks.single())
                                    .build()
                            )
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
