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

package net.impactdev.impactor.sponge.scoreboard;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.events.PlatformBus;
import net.impactdev.impactor.api.scoreboard.events.RegisteredEvent;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.utilities.functional.TriFunction;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.Order;

import java.util.UUID;
import java.util.function.BiFunction;

public class SpongeBus extends PlatformBus<Event> {

    @Override
    protected String getPlatformType() {
        return "Sponge";
    }

    @Override
    public <E extends Event> TriFunction<Updatable, UUID, ListeningFrame.EventHandler<E>, RegisteredEvent> getRegisterHandler(TypeToken<E> type) {
        return (line, assignee, handler) -> {
            EventListener<E> listener = event -> handler.process(line, assignee, event);
            Sponge.eventManager().registerListener(EventListenerRegistration.builder(type)
                    .listener(listener)
                    .plugin(SpongeImpactorPlugin.getInstance().getPluginContainer())
                    .order(Order.DEFAULT)
                    .build()
            );
            return new RegisteredEvent(listener);
        };
    }

    @Override
    public void shutdown(RegisteredEvent registration) {
        Sponge.eventManager().unregisterListeners(registration.getRegistration());
    }
}
