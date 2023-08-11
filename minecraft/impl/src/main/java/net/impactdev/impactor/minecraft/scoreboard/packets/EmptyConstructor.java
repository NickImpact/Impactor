package net.impactdev.impactor.minecraft.scoreboard.packets;

@FunctionalInterface
public interface EmptyConstructor<T> {

    T newInstance() throws Exception;

}
