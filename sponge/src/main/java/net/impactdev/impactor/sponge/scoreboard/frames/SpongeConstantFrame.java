package net.impactdev.impactor.sponge.scoreboard.frames;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.frames.types.ConstantFrame;
import net.impactdev.impactor.api.services.text.MessageService;
import net.kyori.adventure.text.Component;

public class SpongeConstantFrame implements ConstantFrame {

    private final Component text;

    public SpongeConstantFrame(SpongeConstantFrameBuilder builder) {
        this.text = builder.text;
    }

    @Override
    public Component getText() {
        return this.text;
    }

    @Override
    public boolean shouldUpdateOnTick() {
        return false;
    }

    public static class SpongeConstantFrameBuilder implements ConstantFrameBuilder {

        private Component text;

        @Override
        public ConstantFrameBuilder raw(String raw, PlaceholderSources sources) {
            MessageService<Component> service = Impactor.getInstance().getRegistry().get(MessageService.class);
            this.text = service.parse(raw, sources);
            return this;
        }

        @Override
        public ConstantFrameBuilder text(Component text) {
            this.text = text;
            return this;
        }

        @Override
        public ConstantFrameBuilder from(ConstantFrame input) {
            this.text = ((SpongeConstantFrame) input).text;
            return this;
        }

        @Override
        public ConstantFrame build() {
            return new SpongeConstantFrame(this);
        }
    }
}
