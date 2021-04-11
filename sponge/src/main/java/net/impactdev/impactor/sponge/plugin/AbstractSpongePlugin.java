package net.impactdev.impactor.sponge.plugin;

import net.impactdev.impactor.api.logging.Logger;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.PluginMetadata;
import net.impactdev.impactor.api.plugin.registry.PluginRegistry;
import net.impactdev.impactor.sponge.logging.SpongeLogger;

public abstract class AbstractSpongePlugin implements ImpactorPlugin {

    private final PluginMetadata metadata;
    private final SpongeLogger pluginLogger;

    public AbstractSpongePlugin(PluginMetadata metadata, org.apache.logging.log4j.Logger fallback) {
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
