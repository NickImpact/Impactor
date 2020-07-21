package com.nickimpact.impactor.bungee.plugin;

import com.nickimpact.impactor.api.logging.Logger;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.api.plugin.PluginMetadata;
import com.nickimpact.impactor.bungee.logging.BungeeLogger;

public abstract class AbstractBungeePlugin implements ImpactorPlugin {

    private final PluginMetadata metadata;
    private final Logger pluginLogger;

    public AbstractBungeePlugin(PluginMetadata metadata, java.util.logging.Logger logger) {
        this.metadata = metadata;
        this.pluginLogger = new BungeeLogger(logger);
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
