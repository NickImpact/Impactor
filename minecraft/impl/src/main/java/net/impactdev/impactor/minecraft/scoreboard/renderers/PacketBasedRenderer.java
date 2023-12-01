package net.impactdev.impactor.minecraft.scoreboard.renderers;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;
import net.impactdev.impactor.minecraft.api.text.AdventureTranslator;
import net.impactdev.impactor.minecraft.platform.sources.ImpactorPlatformPlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class PacketBasedRenderer implements ScoreboardRenderer {

    @Override
    public void objective(PlatformPlayer viewer, Objective.Displayed objective) {
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
    public void line(PlatformPlayer viewer, ScoreboardLine.Displayed line) {
        PlayerTeam team = new PlayerTeam(null, viewer.uuid().toString());
        team.setDisplayName(AdventureTranslator.toNative(line.text()));
        team.setColor(ChatFormatting.WHITE);

        team.setNameTagVisibility(Team.Visibility.ALWAYS);
        team.setCollisionRule(Team.CollisionRule.ALWAYS);

        ClientboundSetPlayerTeamPacket packet = ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, false);

        ((ImpactorPlatformPlayer) viewer).asMinecraftPlayer().ifPresent(player -> {
            player.connection.send(packet);
        });
    }

    @Override
    public void show(AssignedScoreboard scoreboard) {
        ((ImpactorPlatformPlayer) scoreboard.viewer()).asMinecraftPlayer().ifPresent(player -> {
            net.minecraft.world.scores.Objective objective = this.createObjective(scoreboard.viewer(), scoreboard.objective());
            ClientboundSetObjectivePacket create = new ClientboundSetObjectivePacket(
                    objective,
                    0
            );

            ClientboundSetDisplayObjectivePacket display = new ClientboundSetDisplayObjectivePacket(2, objective);
            player.connection.send(create);
            player.connection.send(display);

            for(ScoreboardLine.Displayed line : scoreboard.lines()) {
                this.line(scoreboard.viewer(), line);
            }
        });
    }

    @Override
    public void hide(AssignedScoreboard scoreboard) {
        ((ImpactorPlatformPlayer) scoreboard.viewer()).asMinecraftPlayer().ifPresent(player -> {
            net.minecraft.world.scores.Objective objective = this.createObjective(scoreboard.viewer(), scoreboard.objective());
            ClientboundSetObjectivePacket remove = new ClientboundSetObjectivePacket(objective, 1);

            player.connection.send(remove);
        });
    }

    @Override
    public void registerTeam(PlatformPlayer viewer) {

    }

    @SuppressWarnings("DataFlowIssue")
    private net.minecraft.world.scores.Objective createObjective(PlatformPlayer viewer, Objective.Displayed objective) {
        return new net.minecraft.world.scores.Objective(
                null,
                viewer.uuid().toString(),
                ObjectiveCriteria.DUMMY,
                AdventureTranslator.toNative(objective.text()),
                ObjectiveCriteria.RenderType.INTEGER
        );
    }

}
