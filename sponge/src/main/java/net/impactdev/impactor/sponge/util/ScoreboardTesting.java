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

package net.impactdev.impactor.sponge.util;

import com.google.common.collect.Maps;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.ImpactorScoreboard;
import net.impactdev.impactor.api.scoreboard.effects.RGBFadeEffect;
import net.impactdev.impactor.api.scoreboard.events.PlatformBus;
import net.impactdev.impactor.api.scoreboard.frames.ScoreboardFrame;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

import java.util.Map;
import java.util.UUID;

public class ScoreboardTesting {

    private final ImpactorScoreboard<ServerPlayer> scoreboard = ImpactorScoreboard.<ServerPlayer>builder()
            .objective(ScoreboardObjective.constant().text(Component.text("Impactor Scoreboard Demo").color(TextColor.color(255, 215, 0))).build())
            .line(ScoreboardLine.refreshing()
                    .text(StringUtils.repeat("\u25A0", 30))
                    .rate(1)
                    .effects(RGBFadeEffect.builder()
                            .frames(90)
                            .step(3)
                            .start(0)
                            .build()
                    )
                    .async()
                    .build(), 15
            )
            .line(ScoreboardLine.constant().text(Component.text("Player Info:").color(NamedTextColor.LIGHT_PURPLE)).build(), 14)
            .line(ScoreboardLine.constant().text("  &fName: &b{{sponge:name}}", PlaceholderSources.empty()).build(), 13)
            .line(ScoreboardLine.empty(), 12)
            .line(ScoreboardLine.constant().text(Component.text("World Info:").color(NamedTextColor.LIGHT_PURPLE)).build(), 11)
            .line(ScoreboardLine.refreshing()
                    .rate(1)
                    .text("  &fDay: &c{{impactor:world_day}}")
                    .build(), 10
            )
            .line(ScoreboardLine.refreshing()
                    .rate(1)
                    .text("  &fTime: &c{{impactor:world_time}}")
                    .build(), 9
            )
            .line(ScoreboardLine.listening()
                    .content(ScoreboardFrame.listening(new TypeToken<MoveEntityEvent>(){})
                            .text("  &fCoordinates: &c{{impactor:coordinates}}")
                            .bus(PlatformBus.getOrCreate())
                            .handler((line, assignee, event) -> {
                                if(event.entity().uniqueId().equals(assignee)) {
                                    line.update();
                                }
                            })
                            .build()
                    )
                    .build(), 8
            )
            .line(ScoreboardLine.empty(), 6)
            .line(ScoreboardLine.constant().text(Component.text("Server Diagnostics:").color(NamedTextColor.LIGHT_PURPLE)).build(), 5)
            .line(ScoreboardLine.constant().text("  &fSponge API Version: &e{{impactor:sponge_api}}", PlaceholderSources.empty()).build(), 4)
            .line(ScoreboardLine.refreshing()
                    .async()
                    .rate(1)
                    .text("  &fTPS: {{impactor:tps}}")
                    .build(), 3
            )
            .line(ScoreboardLine.refreshing()
                    .async()
                    .rate(1)
                    .text("  &fMSpT: {{impactor:mspt}}")
                    .build(), 2
            )
            .line(ScoreboardLine.refreshing()
                    .async()
                    .rate(1)
                    .text("  &fMemory: &b{{impactor:memory_used}}&7/&b{{impactor:memory_total}}")
                    .build(), 1
            )
            .complete()
            .build();

    private final Map<UUID, ImpactorScoreboard<ServerPlayer>> registrations = Maps.newHashMap();

    @Listener
    public void login(ServerSideConnectionEvent.Join event) {
        ImpactorScoreboard<ServerPlayer> assigned = this.scoreboard.assignTo(event.player());
        this.registrations.put(event.player().uniqueId(), assigned);
        assigned.show();
    }

    @Listener
    public void disconnect(ServerSideConnectionEvent.Disconnect event) {
        this.registrations.get(event.player().uniqueId()).hide();
    }

}
