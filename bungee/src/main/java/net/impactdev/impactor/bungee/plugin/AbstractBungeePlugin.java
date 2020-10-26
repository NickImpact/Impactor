package net.impactdev.impactor.bungee.plugin;

import net.impactdev.impactor.api.logging.Logger;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.PluginMetadata;
import net.impactdev.impactor.api.plugin.registry.PluginRegistry;
import net.impactdev.impactor.bungee.logging.BungeeLogger;

public abstract class AbstractBungeePlugin implements ImpactorPlugin {

    private final PluginMetadata metadata;
    private final Logger pluginLogger;

    public AbstractBungeePlugin(PluginMetadata metadata, java.util.logging.Logger logger) {
        this.metadata = metadata;
        this.pluginLogger = new BungeeLogger(logger);

        PluginRegistry.register(this);
    }

    @Override
    public PluginMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public Logger getPluginLogger() {
        return this.pluginLogger;
    }

}
