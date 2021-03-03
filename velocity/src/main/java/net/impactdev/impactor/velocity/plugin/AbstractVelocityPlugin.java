package net.impactdev.impactor.velocity.plugin;

import net.impactdev.impactor.api.logging.Logger;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.PluginMetadata;

public class AbstractVelocityPlugin implements ImpactorPlugin {

    private final PluginMetadata metadata;
    private final Logger logger;

    public AbstractVelocityPlugin(PluginMetadata metadata, Logger logger) {
        this.metadata = metadata;
        this.logger = logger;
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
