package net.impactdev.impactor.sponge.text.processors.gradients;

import net.impactdev.impactor.api.utilities.Builder;
import net.impactdev.impactor.sponge.text.processors.ComponentProcessor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Arrays;
import java.util.LinkedList;

public abstract class GradientProcessor<T> implements ComponentProcessor<T> {

    protected final LinkedList<TextColor> colors;

    protected GradientProcessor(GradientProcessorBuilder<? extends GradientProcessor<T>> builder) {
        this.colors = builder.colors;
    }

    public static abstract class GradientProcessorBuilder<T extends GradientProcessor<?>> implements Builder<T, GradientProcessorBuilder<T>> {

        protected final LinkedList<TextColor> colors = new LinkedList<>();

        public GradientProcessorBuilder<T> colors(TextColor... colors) {
            this.colors.addAll(Arrays.asList(colors));
            return this;
        }

    }

}
