package net.impactdev.impactor.sponge.scoreboard.frames;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.frames.types.RefreshingFrame;
import net.impactdev.impactor.api.services.text.MessageService;
import net.kyori.adventure.text.Component;

public class SpongeRefreshingFrame implements RefreshingFrame {

    private final String raw;
    private final PlaceholderSources sources;

    public SpongeRefreshingFrame(SpongeRefreshingFrameBuilder builder) {
        this.raw = builder.raw;
        this.sources = builder.sources;
    }

    @Override
    public Component getText() {
        MessageService<Component> service = Impactor.getInstance().getRegistry().get(MessageService.class);
        return service.parse(this.raw, this.sources);
    }

    @Override
    public boolean shouldUpdateOnTick() {
        return true;
    }

    @Override
    public void initialize(Updatable parent) {}

    @Override
    public void shutdown() {}

    public static class SpongeRefreshingFrameBuilder implements RefreshingFrameBuilder {

        private String raw;
        private PlaceholderSources sources;

        @Override
        public RefreshingFrameBuilder raw(String raw) {
            this.raw = raw;
            return this;
        }

        @Override
        public RefreshingFrameBuilder sources(PlaceholderSources sources) {
            this.sources = sources;
            return this;
        }

        @Override
        public RefreshingFrameBuilder from(RefreshingFrame input) {
            this.raw = ((SpongeRefreshingFrame) input).raw;
            return this;
        }

        @Override
        public RefreshingFrame build() {
            return new SpongeRefreshingFrame(this);
        }

    }
}
