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

import joptsimple.internal.Strings;
import net.impactdev.impactor.api.scheduler.Ticks;
import net.impactdev.impactor.api.scheduler.v2.Scheduler;
import net.impactdev.impactor.api.scoreboards.Scoreboard;
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.api.scoreboards.display.formatters.styling.rgb.ColorCycle;
import net.impactdev.impactor.api.scoreboards.display.text.ComponentElement;
import net.impactdev.impactor.api.scoreboards.display.text.ScoreboardComponent;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;
import net.impactdev.impactor.api.scoreboards.score.Score;
import net.impactdev.impactor.api.scoreboards.updaters.scheduled.ScheduledUpdater;
import net.impactdev.impactor.api.text.TextProcessor;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.concurrent.TimeUnit;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;

public final class DevScoreboard {

    private static boolean scoreReversed;
    public static final Scoreboard SCOREBOARD = Scoreboard.builder()
            .renderer(ScoreboardRenderer.packets())
            .objective(Objective.builder()
                    .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                    .text(ScoreboardComponent.create(text("»").color(NamedTextColor.GRAY))
                            .append(space())
                            .append(ComponentElement.create(
                                    ColorCycle.configure().frames(90).increment(3).build(),
                                    viewer -> text("Impactor Scoreboard Test")
                            ))
                            .append(space())
                            .append(text("«"))
                    )
                    .build()
            )
            .line(ScoreboardLine.builder()
                    .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                    .score(Score.of(15))
                    .text(ScoreboardComponent.create(ComponentElement.create(
                            ColorCycle.configure().frames(90).increment(3).phase(0).build(),
                            viewer -> text(Strings.repeat('■', 20))
                    )))
                    .build()
            )
            .line(ScoreboardLine.builder()
                    .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                    .score(Score.of(14))
                    .text(ScoreboardComponent.create(ComponentElement.create(
                            ColorCycle.configure().frames(90).increment(3).phase(10).build(),
                            viewer -> text(Strings.repeat('■', 20))
                    )))
                    .build()
            )
            .line(ScoreboardLine.builder()
                    .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                    .score(Score.of(13))
                    .text(ScoreboardComponent.create(ComponentElement.create(
                            ColorCycle.configure().frames(90).increment(3).phase(20).build(),
                            viewer -> text(Strings.repeat('■', 20))
                    )))
                    .build()
            )
            .line(ScoreboardLine.builder()
                    .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                    .score(Score.of(12))
                    .text(ScoreboardComponent.create(ComponentElement.create(
                            ColorCycle.configure().frames(90).increment(3).phase(30).build(),
                            viewer -> text(Strings.repeat('■', 20))
                    )))
                    .build()
            )
            .line(ScoreboardLine.builder()
                    .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                    .score(Score.of(11))
                    .text(ScoreboardComponent.create(ComponentElement.create(
                            ColorCycle.configure().frames(90).increment(3).phase(40).build(),
                            viewer -> text(Strings.repeat('■', 20))
                    )))
                    .build()
            )
            .line(ScoreboardLine.builder()
                    .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                    .score(Score.of(10))
                    .text(ScoreboardComponent.create(ComponentElement.create(
                            ColorCycle.configure().frames(90).increment(3).phase(50).build(),
                            viewer -> text(Strings.repeat('■', 20))
                    )))
                    .build()
            )
            .line(ScoreboardLine.builder()
                    .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                    .score(Score.of(9))
                    .text(ScoreboardComponent.create(ComponentElement.create(
                            ColorCycle.configure().frames(90).increment(3).phase(60).build(),
                            viewer -> text(Strings.repeat('■', 20))
                    )))
                    .build()
            )
            .line(ScoreboardLine.builder().text(ScoreboardComponent.create(empty())).score(Score.of(8)).build())
            .line(ScoreboardLine.builder()
                    .text(ScoreboardComponent.create(text("Updating Score")))
                    .updater(ScheduledUpdater.scheduler(Scheduler.SYNCHRONOUS).repeating(3, TimeUnit.SECONDS))
                    .score(4)
                    .onTickLine((viewer, score) -> {
                        int value = score.value();
                        if(value == 7) {
                            scoreReversed = true;
                        } else if(value == 4) {
                            scoreReversed = false;
                        }

                        score.set(value + ((scoreReversed ? -1 : 1)));
                    })
                    .build()
            )
            .line(ScoreboardLine.builder().text(ScoreboardComponent.create(empty())).score(Score.of(3)).build())
            .line(ScoreboardLine.builder()
                    .score(Score.of(2))
                    .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                    .text(ScoreboardComponent.create(text("TPS: ").color(NamedTextColor.GRAY))
                            .append(ComponentElement.create(viewer -> {
                                TextProcessor processor = TextProcessor.mini();
                                return processor.parse("<green><impactor:tps>");
                            }))
                    )
                    .build()
            )
            .line(ScoreboardLine.builder()
                    .score(Score.of(1))
                    .text(ScoreboardComponent.create(text("MSPT: ").color(NamedTextColor.GRAY))
                            .append(ComponentElement.create(viewer -> {
                                TextProcessor processor = TextProcessor.mini();
                                return processor.parse("<green><impactor:mspt>");
                            }))
                    )
                    .updater(ScheduledUpdater.scheduler(Scheduler.ASYNCHRONOUS).repeating(Ticks.single()))
                    .build()
            )
            .build();

}
