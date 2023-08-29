package net.impactdev.impactor.minecraft.scheduler;

import java.util.Optional;

public interface Task {

    Runnable action();

    void tick();

    boolean valid();

    void execute();

    interface Accepts<E extends Task> extends Task {

        Task with(E child);

        Optional<E> child();

    }

}
