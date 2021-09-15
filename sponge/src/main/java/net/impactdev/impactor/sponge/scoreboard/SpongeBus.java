package net.impactdev.impactor.sponge.scoreboard;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.events.PlatformBus;
import net.impactdev.impactor.api.scoreboard.events.RegisteredEvent;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.Order;

import java.util.function.BiFunction;

public class SpongeBus extends PlatformBus<Event> {

    @Override
    protected String getPlatformType() {
        return "Sponge";
    }

    @Override
    public <E extends Event> BiFunction<Updatable, ListeningFrame.EventHandler<E>, RegisteredEvent> getRegisterHandler(TypeToken<E> type) {
        return (line, handler) -> {
            EventListener<E> listener = event -> handler.process(line, event);
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
