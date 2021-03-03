package net.impactdev.impactor.velocity.event;

import com.velocitypowered.api.plugin.PluginContainer;
import net.impactdev.impactor.common.event.AbstractEventBus;
import net.impactdev.impactor.velocity.VelocityImpactorBootstrap;

public class VelocityEventBus extends AbstractEventBus<PluginContainer> {

    private final VelocityImpactorBootstrap bootstrap;

    public VelocityEventBus(VelocityImpactorBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    protected PluginContainer checkPlugin(Object plugin) throws IllegalArgumentException {
        if(plugin instanceof PluginContainer) {
            return (PluginContainer) plugin;
        }

        PluginContainer container = this.bootstrap.getProxy().getPluginManager().fromInstance(plugin).orElse(null);
        if(container != null) {
            return container;
        }

        throw new IllegalArgumentException("Object " + plugin + " (" + plugin.getClass().getName() + ") is not a plugin.");
    }

}
