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

package net.impactdev.impactor.sponge.scoreboard.frames;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.events.Bus;
import net.impactdev.impactor.api.scoreboard.events.RegisteredEvent;
import net.impactdev.impactor.api.scoreboard.frames.ScoreboardFrame;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.services.text.MessageService;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.NoSuchElementException;
import java.util.UUID;

public class SpongeListeningFrame<L> extends AbstractSpongeFrame implements ListeningFrame<L> {

    private String raw;
    private Bus<? super L> bus;
    private Class<L> type;
    private EventHandler<L> handler;
    private PlaceholderSources sources;

    private transient RegisteredEvent registration;
    private transient UUID source;

    public SpongeListeningFrame(SpongeListeningFrameBuilder<L> builder) {
        this.raw = builder.raw;
        this.bus = builder.bus;
        this.type = builder.type;
        this.handler = builder.handler;
        this.sources = builder.sources;
    }

    @Override
    public Component getText() {
        MessageService service = Impactor.getInstance().getRegistry().get(MessageService.class);
        return service.parse(this.raw, this.sources);
    }

    @Override
    public boolean shouldUpdateOnTick() {
        return false;
    }

    @Override
    public void initialize(Updatable parent) {
        this.registration = this.bus.getRegisterHandler(this.type).process(parent, this.source, this.handler);
    }

    @Override
    public void shutdown() {
        this.bus.shutdown(this.registration);
    }

    @Override
    public Class<L> getListenerType() {
        return this.type;
    }

    @Override
    public EventHandler<L> getEventHandler() {
        return this.handler;
    }

    @Override
    public void provideSource(UUID uuid) {
        this.source = uuid;
        this.sources = PlaceholderSources.builder()
                .from(this.sources)
                .appendIfAbsent(ServerPlayer.class, () -> Sponge.server().player(uuid).orElseThrow(NoSuchElementException::new))
                .build();
    }

    @Override
    public ScoreboardFrame copy() {
        SpongeListeningFrame<L> clone = new SpongeListeningFrame<>(new SpongeListeningFrameBuilder<>());
        clone.raw = this.raw;
        clone.bus = this.bus;
        clone.type = this.type;
        clone.handler = this.handler;
        clone.sources = this.sources;
        return clone;
    }

    public static class SpongeListeningFrameBuilder<L> implements ListeningFrameBuilder<L> {

        private Class<L> type;
        private Bus<? super L> bus;
        private String raw;
        private EventHandler<L> handler;
        private PlaceholderSources sources = PlaceholderSources.empty();

        public SpongeListeningFrameBuilder() {}

        private SpongeListeningFrameBuilder(Class<L> type) {
            this.type = type;
        }

        @Override
        public <E> ListeningFrameBuilder<E> type(Class<E> event) {
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
        public ListeningFrame<L> build() {
            return new SpongeListeningFrame<>(this);
        }

    }
}
