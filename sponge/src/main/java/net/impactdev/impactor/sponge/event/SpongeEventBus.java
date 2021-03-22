package net.impactdev.impactor.sponge.event;

import net.impactdev.impactor.common.event.AbstractEventBus;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

public final class SpongeEventBus extends AbstractEventBus<PluginContainer> {

    @Override
    protected PluginContainer checkPlugin(Object plugin) throws IllegalArgumentException {
        if (plugin instanceof PluginContainer) {
            return (PluginContainer) plugin;
        }

        PluginContainer pluginContainer = Sponge.pluginManager().fromInstance(plugin).orElse(null);
        if (pluginContainer != null) {
            return pluginContainer;
        }

        throw new IllegalArgumentException("Object " + plugin + " (" + plugin.getClass().getName() + ") is not a plugin.");
    }

}
