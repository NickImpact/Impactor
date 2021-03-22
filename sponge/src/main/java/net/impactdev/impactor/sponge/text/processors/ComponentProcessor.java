package net.impactdev.impactor.sponge.text.processors;

import net.kyori.adventure.text.Component;

public interface ComponentProcessor<T> {

    Component process(T input);

}
