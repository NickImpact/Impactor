package net.impactdev.impactor.minecraft.scoreboard.resolvers;

import net.impactdev.impactor.api.text.transforming.transformers.TextTransformer;
import net.impactdev.impactor.scoreboards.updaters.listener.ListenerResolver;
import net.kyori.adventure.text.Component;
import net.kyori.event.EventSubscription;

import java.util.function.Supplier;

public class ImpactorListenerResolver implements ListenerResolver {

    private final Supplier<Component> provider;
    private final TextTransformer transformer;

    private final EventSubscription subscription;
    private final Component component;

    @Override
    public EventSubscription subscription() {
        return this.subscription;
    }

    @Override
    public Component resolve() {
        return this.provider.get();
    }

    @Override
    public void shutdown() {
        this.subscription().unsubscribe();
    }


}
