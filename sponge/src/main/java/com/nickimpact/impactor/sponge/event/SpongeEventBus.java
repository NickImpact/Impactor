package com.nickimpact.impactor.sponge.event;

import com.nickimpact.impactor.common.event.AbstractEventBus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

public final class SpongeEventBus extends AbstractEventBus<PluginContainer> {

    @Override
    protected PluginContainer checkPlugin(Object plugin) throws IllegalArgumentException {
        if (plugin instanceof PluginContainer) {
            return (PluginContainer) plugin;
        }

        PluginContainer pluginContainer = Sponge.getPluginManager().fromInstance(plugin).orElse(null);
        if (pluginContainer != null) {
            return pluginContainer;
        }

        throw new IllegalArgumentException("Object " + plugin + " (" + plugin.getClass().getName() + ") is not a plugin.");
    }

}
