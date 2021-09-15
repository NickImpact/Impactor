package net.impactdev.impactor.sponge.scoreboard.objective.types;

import com.google.common.base.Preconditions;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.objective.types.ConstantObjective;
import net.impactdev.impactor.api.services.text.MessageService;
import net.kyori.adventure.text.Component;

public class SpongeConstantObjective implements ConstantObjective {

    private final Component text;

    private SpongeConstantObjective(SpongeConstantObjectiveBuilder builder) {
        this.text = builder.text;
    }

    @Override
    public Component getText() {
        return this.text;
    }

    public static class SpongeConstantObjectiveBuilder implements ConstantObjectiveBuilder {

        private Component text;

        @Override
        public SpongeConstantObjectiveBuilder raw(String raw, PlaceholderSources sources) {
            MessageService<Component> service = Impactor.getInstance().getRegistry().get(MessageService.class);
            this.text = service.parse(raw, sources);
            return this;
        }

        @Override
        public SpongeConstantObjectiveBuilder text(Component text) {
            this.text = text;
            return this;
        }

        @Override
        public ConstantObjectiveBuilder from(ConstantObjective input) {
            Preconditions.checkArgument(input instanceof SpongeConstantObjective);
            this.text = input.getText();
            return this;
        }

        @Override
        public ConstantObjective build() {
            return new SpongeConstantObjective(this);
        }
    }
}
