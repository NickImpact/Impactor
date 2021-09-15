package net.impactdev.impactor.sponge.scoreboard.frames;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.events.Bus;
import net.impactdev.impactor.api.scoreboard.events.RegisteredEvent;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.services.text.MessageService;
import net.kyori.adventure.text.Component;

public class SpongeListeningFrame<L> implements ListeningFrame<L> {

    private final String raw;
    private final Bus<? super L> bus;
    private final TypeToken<L> type;
    private final EventHandler<L> handler;
    private final PlaceholderSources sources;

    private transient RegisteredEvent registration;

    public SpongeListeningFrame(SpongeListeningFrameBuilder<L> builder) {
        this.raw = builder.raw;
        this.bus = builder.bus;
        this.type = builder.type;
        this.handler = builder.handler;
        this.sources = builder.sources;
    }

    @Override
    public Component getText() {
        MessageService<Component> service = Impactor.getInstance().getRegistry().get(MessageService.class);
        return service.parse(this.raw, this.sources);
    }

    @Override
    public boolean shouldUpdateOnTick() {
        return false;
    }

    @Override
    public void initialize(Updatable parent) {
        this.registration = this.bus.getRegisterHandler(this.type).apply(parent, this.handler);
    }

    @Override
    public void shutdown() {
        this.bus.shutdown(this.registration);
    }

    @Override
    public TypeToken<L> getListenerType() {
        return this.type;
    }

    @Override
    public EventHandler<L> getEventHandler() {
        return this.handler;
    }

    public static class SpongeListeningFrameBuilder<L> implements ListeningFrameBuilder<L> {

        private TypeToken<L> type;
        private Bus<? super L> bus;
        private String raw;
        private EventHandler<L> handler;
        private PlaceholderSources sources;

        public SpongeListeningFrameBuilder() {}

        private SpongeListeningFrameBuilder(TypeToken<L> type) {
            this.type = type;
        }

        @Override
        public <E> ListeningFrameBuilder<E> type(TypeToken<E> event) {
            return new SpongeListeningFrameBuilder<>(event);
        }

        @Override
        public ListeningFrameBuilder<L> bus(Bus<? super L> bus) {
            this.bus = bus;
            return this;
        }

        @Override
        public ListeningFrameBuilder<L> text(String raw) {
            this.raw = raw;
            return this;
        }

        @Override
        public ListeningFrameBuilder<L> handler(EventHandler<L> handler) {
            this.handler = handler;
            return this;
        }

        @Override
        public ListeningFrameBuilder<L> sources(PlaceholderSources sources) {
            this.sources = sources;
            return this;
        }

        @Override
        public ListeningFrameBuilder<L> from(ListeningFrame<L> input) {
            this.raw = ((SpongeListeningFrame<L>) input).raw;
            this.handler = input.getEventHandler();
            this.bus = ((SpongeListeningFrame<L>) input).bus;
            return this;
        }

        @Override
        public ListeningFrame<L> build() {
            return new SpongeListeningFrame<>(this);
        }

    }
}
