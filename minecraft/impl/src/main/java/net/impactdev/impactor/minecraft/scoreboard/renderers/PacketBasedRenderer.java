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

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;
import net.impactdev.impactor.minecraft.api.text.AdventureTranslator;
import net.impactdev.impactor.minecraft.platform.sources.ImpactorPlatformPlayer;
import net.impactdev.impactor.minecraft.scoreboard.assigned.AssignedScoreboardImpl;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.Arrays;

public class PacketBasedRenderer implements ScoreboardRenderer {

    private static final Pointer<net.minecraft.world.scores.Objective> OBJECTIVE = Pointer.pointer(
            net.minecraft.world.scores.Objective.class,
            Key.key("impactor", "objective")
    );
    private static final Pointer<PlayerTeam> TEAM = Pointer.pointer(
            PlayerTeam.class,
            Key.key("impactor", "team")
    );
    private static final Pointer<ChatFormatting> COLOR = Pointer.pointer(
            ChatFormatting.class,
            Key.key("impactor", "color")
    );

    @Override
    public void objective(AssignedScoreboard scoreboard, Objective.Displayed objective) {
        net.minecraft.world.scores.Objective minecraft = scoreboard.require(OBJECTIVE);
        minecraft.setDisplayName(AdventureTranslator.toNative(objective.text()));
        ClientboundSetObjectivePacket update = new ClientboundSetObjectivePacket(minecraft, 2);

        this.publish(scoreboard.viewer(), update);
    }

    @Override
    public void line(AssignedScoreboard scoreboard, ScoreboardLine.Displayed line) {
        net.minecraft.world.scores.Objective objective = scoreboard.require(OBJECTIVE);
        PlayerTeam team = line.require(TEAM);
        team.setPlayerPrefix(AdventureTranslator.toNative(line.text()));

        final ClientboundSetPlayerTeamPacket update = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, false);
        final ClientboundSetScorePacket score = new ClientboundSetScorePacket(
                ServerScoreboard.Method.CHANGE,
                objective.getName(),
                team.getName(),
                line.delegate().score().value()
        );

        this.publish(scoreboard.viewer(), update, score);
    }

    @Override
    public void show(AssignedScoreboard scoreboard) {
        net.minecraft.world.scores.Objective objective = this.createObjective(scoreboard.viewer(), scoreboard.objective());
        ClientboundSetObjectivePacket create = new ClientboundSetObjectivePacket(objective, 0);
        ClientboundSetDisplayObjectivePacket display = new ClientboundSetDisplayObjectivePacket(Scoreboard.DISPLAY_SLOT_SIDEBAR, objective);

        scoreboard.with(OBJECTIVE, objective);
        this.publish(scoreboard.viewer(), create, display);

        scoreboard.lines().forEach(line -> this.createTeam(scoreboard, line));
    }

    @Override
    public void hide(AssignedScoreboard scoreboard) {
        ((ImpactorPlatformPlayer) scoreboard.viewer()).asMinecraftPlayer().ifPresent(player -> {
            net.minecraft.world.scores.Objective objective = scoreboard.require(OBJECTIVE);
            ClientboundSetObjectivePacket remove = new ClientboundSetObjectivePacket(objective, 1);

            player.connection.send(remove);
        });
    }

    @Override
    public void createTeam(AssignedScoreboard scoreboard, ScoreboardLine.Displayed line) {
        ChatFormatting formatting = this.translate(scoreboard, AssignedScoreboardImpl.class).colors().select();
        line.with(COLOR, formatting);

        Scoreboard minecraft = new Scoreboard();
        PlayerTeam team = new PlayerTeam(minecraft, "IMD-" + formatting.getId());
        line.with(TEAM, team);
        team.setColor(formatting);
        team.setNameTagVisibility(Team.Visibility.ALWAYS);
        team.setCollisionRule(Team.CollisionRule.ALWAYS);

        final ClientboundSetPlayerTeamPacket packet = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true);
        final ClientboundSetPlayerTeamPacket member = ClientboundSetPlayerTeamPacket.createPlayerPacket(
                team,
                AssignedScoreboardImpl.MEMBER_PREFIX + formatting + ChatFormatting.RESET,
                ClientboundSetPlayerTeamPacket.Action.ADD
        );
        this.publish(scoreboard.viewer(), packet, member);
    }

    @Override
    public void destroyTeam(AssignedScoreboard scoreboard, ScoreboardLine.Displayed line) {
        PlayerTeam team = line.require(TEAM);
        ClientboundSetPlayerTeamPacket packet = ClientboundSetPlayerTeamPacket.createRemovePacket(team);

        this.publish(scoreboard.viewer(), packet);
    }

    private net.minecraft.world.scores.Objective createObjective(PlatformPlayer viewer, Objective.Displayed objective) {
        return new net.minecraft.world.scores.Objective(
                new Scoreboard(),
                viewer.uuid().toString(),
                ObjectiveCriteria.DUMMY,
                AdventureTranslator.toNative(objective.text()),
                ObjectiveCriteria.RenderType.INTEGER
        );
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
