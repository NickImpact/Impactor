package net.impactdev.impactor.api.scoreboard.events;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.event.ImpactorEvent;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;

import java.util.Optional;
import java.util.function.BiFunction;

public class ImpactorBus implements Bus<ImpactorEvent> {

    private static ImpactorBus instance;

    public static ImpactorBus getOrCreate() {
        return Optional.ofNullable(instance).orElse(instance = new ImpactorBus());
    }

    @Override
    public String getID() {
        return "Impactor";
    }

    @Override
    public <E extends ImpactorEvent> BiFunction<Updatable, ListeningFrame.EventHandler<E>, RegisteredEvent> getRegisterHandler(TypeToken<E> type) {
        return (line, handler) -> new RegisteredEvent(Impactor.getInstance().getEventBus().subscribe(type, event -> handler.process(line, event)));
    }

    @Override
    public void shutdown(RegisteredEvent registration) {
        //Impactor.getInstance().getEventBus()
    }

}
