package net.impactdev.impactor.api.scoreboard.events;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;

import java.util.function.BiFunction;

public interface Bus<L> {

    String getID();

    <E extends L> BiFunction<Updatable, ListeningFrame.EventHandler<E>, RegisteredEvent> getRegisterHandler(TypeToken<E> type);

    void shutdown(RegisteredEvent registration);

}
