package com.nickimpact.impactor.sponge.plugin;

import com.nickimpact.impactor.api.logging.Logger;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.api.plugin.PluginMetadata;
import com.nickimpact.impactor.api.plugin.registry.PluginRegistry;
import com.nickimpact.impactor.sponge.logging.SpongeLogger;

public abstract class AbstractSpongePlugin implements ImpactorPlugin {

    private final PluginMetadata metadata;
    private final SpongeLogger pluginLogger;

    public AbstractSpongePlugin(PluginMetadata metadata, org.slf4j.Logger fallback) {
        this.metadata = metadata;
        this.pluginLogger = new SpongeLogger(this, fallback);

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
