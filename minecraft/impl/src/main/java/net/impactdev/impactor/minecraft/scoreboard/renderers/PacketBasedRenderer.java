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

package net.impactdev.impactor.minecraft.scoreboard.renderers;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;
import net.impactdev.impactor.minecraft.api.text.AdventureTranslator;
import net.impactdev.impactor.minecraft.platform.sources.ImpactorPlatformPlayer;
import net.impactdev.impactor.minecraft.mixins.MixinBridge;
import net.impactdev.impactor.minecraft.scoreboard.assigned.ScoreboardComponents;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class PacketBasedRenderer implements ScoreboardRenderer {

    private static final Pointer<Integer> TEAM_INDEX = Pointer.pointer(Integer.class, Key.key("impactor", "team-index"));

    @Override
    public void objective(AssignedScoreboard scoreboard, Objective.Displayed objective) {
        ClientboundSetObjectivePacket update = new ClientboundSetObjectivePacket(ScoreboardComponents.OBJECTIVE, 2);
        Impactor.instance().factories().provide(MixinBridge.class).setObjectiveTitle(update, objective.text());

        this.publish(scoreboard.viewer(), update);
    }

    @Override
    public void line(AssignedScoreboard scoreboard, ScoreboardLine.Displayed line) {
        PlayerTeam team = ScoreboardComponents.team(line.require(TEAM_INDEX));
        applyLineTextToPacket(scoreboard, line, team, false);
    }

    private void applyLineTextToPacket(AssignedScoreboard scoreboard, ScoreboardLine.Displayed line, PlayerTeam team, boolean create) {
        final ClientboundSetPlayerTeamPacket update = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, create);
        Impactor.instance().factories().provide(MixinBridge.class).setPlayerTeamPrefix(update, line.text());

        final ClientboundSetScorePacket score = new ClientboundSetScorePacket(
                ServerScoreboard.Method.CHANGE,
                ScoreboardComponents.OBJECTIVE_NAME,
                ScoreboardComponents.fakeName(line.require(TEAM_INDEX)),
                line.score().value()
        );

        this.publish(scoreboard.viewer(), update, score);
    }

    @Override
    public void show(AssignedScoreboard scoreboard) {
        ClientboundSetObjectivePacket create = new ClientboundSetObjectivePacket(ScoreboardComponents.OBJECTIVE, 0);
        ClientboundSetDisplayObjectivePacket display = new ClientboundSetDisplayObjectivePacket(Scoreboard.DISPLAY_SLOT_SIDEBAR, ScoreboardComponents.OBJECTIVE);

        this.publish(scoreboard.viewer(), create, display);

        AtomicInteger index = new AtomicInteger();
        scoreboard.lines().forEach(line -> {
            line.with(TEAM_INDEX, index.getAndIncrement());
            this.createTeam(scoreboard, line);
        });
    }

    @Override
    public void hide(AssignedScoreboard scoreboard) {
        ((ImpactorPlatformPlayer) scoreboard.viewer()).asMinecraftPlayer().ifPresent(player -> {
            ClientboundSetObjectivePacket remove = new ClientboundSetObjectivePacket(ScoreboardComponents.OBJECTIVE, 1);

            player.connection.send(remove);
        });
    }

    @Override
    public void createTeam(AssignedScoreboard scoreboard, ScoreboardLine.Displayed line) {
        PlayerTeam team = ScoreboardComponents.team(line.require(TEAM_INDEX));
        applyLineTextToPacket(scoreboard, line, team, true);
    }

    @Override
    public void destroyTeam(AssignedScoreboard scoreboard, ScoreboardLine.Displayed line) {
        PlayerTeam team = ScoreboardComponents.team(line.require(TEAM_INDEX));
        ClientboundSetPlayerTeamPacket packet = ClientboundSetPlayerTeamPacket.createRemovePacket(team);

        this.publish(scoreboard.viewer(), packet);
    }

    private void publish(PlatformPlayer player, Packet<?>... packets) {
        ((ImpactorPlatformPlayer) player).asMinecraftPlayer()
                .map(p -> p.connection)
                .ifPresent(connection -> Arrays.stream(packets).forEach(connection::send));
    }

    private <I, T extends I> T translate(I input, Class<T> target) {
        return target.cast(input);
    }

}
