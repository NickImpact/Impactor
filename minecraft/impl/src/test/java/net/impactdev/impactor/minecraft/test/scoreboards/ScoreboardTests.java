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

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.scheduler.Ticks;
import net.impactdev.impactor.api.scheduler.v2.Scheduler;
import net.impactdev.impactor.api.scheduler.v2.Schedulers;
import net.impactdev.impactor.api.scoreboards.Scoreboard;
import net.impactdev.impactor.api.scoreboards.display.formatters.rgb.ColorCycle;
import net.impactdev.impactor.api.scoreboards.display.formatters.rgb.Rainbow;
import net.impactdev.impactor.api.scoreboards.display.resolvers.scheduled.ScheduledResolverConfiguration;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;
import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.score.formatters.BlankFormatter;
import net.impactdev.impactor.api.text.TextProcessor;
import org.junit.jupiter.api.Test;

public final class ScoreboardTests {

    @Test
    public void create() throws InterruptedException {
        Objective objective = Objective.builder()
                .resolver(ScheduledResolverConfiguration.builder()
                        .provider((viewer, context) -> TextProcessor.mini().parse(viewer, "<impactor:name> Hello!", context))
                        .formatter(ColorCycle.configure().frames(90).increment(3).build())
                        .scheduler(Schedulers.require(Scheduler.ASYNCHRONOUS))
                        .repeating(Ticks.single())
                        .build()
                )
                .formatter(BlankFormatter.INSTANCE)
                .build();

        TestRenderer renderer = new TestRenderer();
        Scoreboard scoreboard = Scoreboard.builder()
                .renderer(renderer)
                .objective(objective)
                .build();

        AssignedScoreboard viewed = scoreboard.assignTo(PlatformPlayer.getOrCreate(PlatformSource.SERVER_UUID));
        viewed.open();

        Thread.sleep(5000);
        viewed.hide();
    }

}
