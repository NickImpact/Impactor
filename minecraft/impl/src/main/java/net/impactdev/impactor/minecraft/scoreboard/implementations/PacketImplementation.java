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

package net.impactdev.impactor.minecraft.scoreboard.implementations;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.scoreboards.relative.RelativeScoreboardLine;
import net.impactdev.impactor.api.scoreboards.relative.RelativeObjective;
import net.impactdev.impactor.api.scoreboards.relative.PlayerScoreboard;
import net.impactdev.impactor.minecraft.api.text.AdventureTranslator;
import net.impactdev.impactor.minecraft.platform.sources.ImpactorPlatformPlayer;
import net.impactdev.impactor.api.scoreboards.ScoreboardImplementation;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public final class PacketImplementation implements ScoreboardImplementation {

    @Override
    public void objective(RelativeObjective objective) {
        ClientboundSetObjectivePacket packet = new ClientboundSetObjectivePacket(
                this.createObjective(viewer, objective),
                0
        );

        ((ImpactorPlatformPlayer) viewer).asMinecraftPlayer().ifPresent(player -> {
            player.connection.send(packet);
        });
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void line(RelativeScoreboardLine line) {
        PlayerTeam team = new PlayerTeam(null, viewer.uuid().toString());
        team.setDisplayName(AdventureTranslator.toNative(line.resolver().resolve()));
        team.setColor(ChatFormatting.WHITE);

        team.setNameTagVisibility(Team.Visibility.ALWAYS);
        team.setCollisionRule(Team.CollisionRule.ALWAYS);

        ClientboundSetPlayerTeamPacket packet = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, false);

        ((ImpactorPlatformPlayer) viewer).asMinecraftPlayer().ifPresent(player -> {
            player.connection.send(packet);
        });
    }

    @Override
    public void show(PlayerScoreboard scoreboard) {
        ((ImpactorPlatformPlayer) viewer).asMinecraftPlayer().ifPresent(player -> {
            net.minecraft.world.scores.Objective objective = this.createObjective(viewer, scoreboard.objective());
            ClientboundSetObjectivePacket create = new ClientboundSetObjectivePacket(
                    objective,
                    0
            );

            ClientboundSetDisplayObjectivePacket display = new ClientboundSetDisplayObjectivePacket(2, objective);
            player.connection.send(create);
            player.connection.send(display);

            for(ScoreboardLine line : scoreboard.lines()) {
                this.line(viewer, line);
            }
        });
    }

    @Override
    public void hide(PlayerScoreboard scoreboard) {
        ((ImpactorPlatformPlayer) viewer).asMinecraftPlayer().ifPresent(player -> {
            net.minecraft.world.scores.Objective objective = this.createObjective(viewer, scoreboard.objective());
            ClientboundSetObjectivePacket remove = new ClientboundSetObjectivePacket(objective, 1);

            player.connection.send(remove);
        });
    }

    @Override
    public void registerTeam(PlatformPlayer viewer) {

    }

    @SuppressWarnings("DataFlowIssue")
    private net.minecraft.world.scores.Objective createObjective(PlatformPlayer viewer, Objective objective) {
        return new net.minecraft.world.scores.Objective(
                null,
                viewer.uuid().toString(),
                ObjectiveCriteria.DUMMY,
                AdventureTranslator.toNative(objective.resolver().resolve()),
                ObjectiveCriteria.RenderType.INTEGER
        );
    }

}
