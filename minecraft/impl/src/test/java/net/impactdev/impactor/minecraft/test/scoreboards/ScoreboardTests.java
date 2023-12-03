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

package net.impactdev.impactor.minecraft.test.scoreboards;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.players.PlatformPlayerService;
import net.impactdev.impactor.api.platform.players.events.ClientConnectionEvent;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.scheduler.Ticks;
import net.impactdev.impactor.api.scheduler.v2.Scheduler;
import net.impactdev.impactor.api.scheduler.v2.Schedulers;
import net.impactdev.impactor.api.scoreboards.Scoreboard;
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.api.scoreboards.display.formatters.rgb.RainbowFormatter;
import net.impactdev.impactor.api.scoreboards.display.resolvers.scheduled.ScheduledResolverConfiguration;
import net.impactdev.impactor.api.scoreboards.display.resolvers.subscribing.Subscribers;
import net.impactdev.impactor.api.scoreboards.display.resolvers.subscribing.SubscriptionConfiguration;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;
import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.score.Score;
import net.impactdev.impactor.api.scoreboards.score.formatters.BlankFormatter;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.minecraft.test.TestPlugin;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public final class ScoreboardTests {

    @Test
//    @Disabled("In development, implementation not ready")
    public void create() throws InterruptedException {
        Objective objective = Objective.builder()
                .resolver(ScheduledResolverConfiguration.builder()
                        .provider(context -> Component.text("Impactor Scoreboard Test"))
                        .formatter(RainbowFormatter.builder().phase(0).increment(3).locked(false).build())
                        .scheduler(Schedulers.require(Scheduler.ASYNCHRONOUS))
                        .repeating(Ticks.single())
                        .build()
                )
                .formatter(BlankFormatter.INSTANCE)
                .build();

//        Objective listening = Objective.builder()
//                .formatter(BlankFormatter.INSTANCE)
//                .resolver(SubscriptionConfiguration.builder()
//                        .provider(context -> Component.empty())
//                        .listenForWithConditions(ClientConnectionEvent.Join.class)
//                        .condition(event -> event.player().uuid().equals(PlatformSource.SERVER_UUID))
//                        .complete()
//                        .subscribe(Subscribers.impactor())
//                        .build()
//                )
//                .build();

        TestRenderer renderer = new TestRenderer();
        Scoreboard scoreboard = Scoreboard.builder()
                .renderer(renderer)
                .objective(objective)
                .build();

        AssignedScoreboard viewed = scoreboard.assignTo(PlatformPlayer.getOrCreate(PlatformSource.SERVER_UUID));
        viewed.open();

        Thread.sleep(5000);
        PrettyPrinter printer = renderer.printer;
        printer.log(TestPlugin.instance().logger());
    }

}
