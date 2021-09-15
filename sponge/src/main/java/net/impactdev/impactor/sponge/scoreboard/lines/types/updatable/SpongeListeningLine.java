package net.impactdev.impactor.sponge.scoreboard.lines.types.updatable;

import net.impactdev.impactor.api.scoreboard.components.LineIdentifier;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.scoreboard.lines.types.ListeningLine;
import net.impactdev.impactor.sponge.scoreboard.lines.AbstractSpongeSBLine;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.objective.Objective;

import java.util.UUID;

public class SpongeListeningLine extends AbstractSpongeSBLine implements ListeningLine {

    private final ListeningFrame<?> frame;

    private final Team team;
    private final Component identifier;

    public SpongeListeningLine(SpongeListeningLineBuilder builder) {
        super(builder.score);
        this.frame = builder.frame;

        this.team = Team.builder().name(UUID.randomUUID().toString().substring(0, 16)).build();
        this.team.addMember(this.identifier = LineIdentifier.generate());
    }

    @Override
    public Component getText() {
        return this.frame.getText();
    }

    @Override
    public void start() {
        this.frame.initialize(this);
    }

    @Override
    public void update() {
        this.team.setPrefix(this.getText());
    }

    @Override
    public void shutdown() {
        this.frame.shutdown();
    }

    @Override
    public ListeningFrame.EventHandler<?> getEventHandler() {
        return this.frame.getEventHandler();
    }

    @Override
    public void setup(Scoreboard scoreboard, Objective objective, ServerPlayer target) {
        objective.findOrCreateScore(this.identifier).setScore(this.getScore());
        scoreboard.registerTeam(this.team);
        this.team.setPrefix(this.getText());
    }

    public static class SpongeListeningLineBuilder implements ListeningBuilder {

        private int score;
        private ListeningFrame<?> frame;

        @Override
        public ListeningBuilder score(int score) {
            this.score = score;
            return this;
        }

        @Override
        public ListeningBuilder content(ListeningFrame<?> frame) {
            this.frame = frame;
            return this;
        }

        @Override
        public ListeningBuilder from(ListeningLine input) {
            this.score = input.getScore();
            this.frame = ((SpongeListeningLine) input).frame;
            return this;
        }

        @Override
        public ListeningLine build() {
            return new SpongeListeningLine(this);
        }
    }

}
