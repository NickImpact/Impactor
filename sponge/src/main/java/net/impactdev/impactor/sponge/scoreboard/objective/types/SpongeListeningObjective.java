package net.impactdev.impactor.sponge.scoreboard.objective.types;

import com.google.common.base.Preconditions;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.scoreboard.objective.types.ListeningObjective;
import net.impactdev.impactor.sponge.scoreboard.objective.AbstractSpongeObjective;
import net.kyori.adventure.text.Component;

public class SpongeListeningObjective extends AbstractSpongeObjective implements ListeningObjective {

    private final ListeningFrame<?> frame;

    private SpongeListeningObjective(SpongeListeningObjectiveBuilder builder) {
        this.frame = builder.frame;
    }

    @Override
    public Component getText() {
        return this.frame.getText();
    }

    @Override
    public ListeningFrame.EventHandler<?> getEventHandler() {
        return this.frame.getEventHandler();
    }

    @Override
    public void start() {
        this.frame.initialize(this);
    }

    @Override
    public void update() {
        this.getDelegate().setDisplayName(this.getText());
    }

    @Override
    public void shutdown() {
        this.frame.shutdown();
    }

    public static class SpongeListeningObjectiveBuilder implements ListeningObjectiveBuilder {

        private ListeningFrame<?> frame;

        @Override
        public ListeningObjectiveBuilder frame(ListeningFrame<?> frame) {
            this.frame = frame;
            return this;
        }

        @Override
        public ListeningObjectiveBuilder from(ListeningObjective input) {
            Preconditions.checkArgument(this.frame instanceof SpongeListeningObjective);
            this.frame = ((SpongeListeningObjective)input).frame;

            return this;
        }

        @Override
        public ListeningObjective build() {
            return new SpongeListeningObjective(this);
        }

    }

}
