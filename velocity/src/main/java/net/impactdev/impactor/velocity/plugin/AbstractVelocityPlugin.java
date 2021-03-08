package net.impactdev.impactor.velocity.plugin;

import net.impactdev.impactor.api.logging.Logger;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.PluginMetadata;
import net.impactdev.impactor.api.plugin.registry.PluginRegistry;
import net.impactdev.impactor.velocity.logging.VelocityLogger;

public class AbstractVelocityPlugin implements ImpactorPlugin {

    private final PluginMetadata metadata;
    private final Logger logger;

    public AbstractVelocityPlugin(PluginMetadata metadata) {
        this.metadata = metadata;
        this.logger = new VelocityLogger(this);

        PluginRegistry.register(this);
    }

    @Override
    public PluginMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public Logger getPluginLogger() {
        return this.logger;
    }
}
