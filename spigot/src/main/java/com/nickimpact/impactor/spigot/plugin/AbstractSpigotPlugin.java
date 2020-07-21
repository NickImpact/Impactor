package com.nickimpact.impactor.spigot.plugin;

import com.nickimpact.impactor.api.logging.Logger;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.api.plugin.PluginMetadata;
import com.nickimpact.impactor.spigot.logging.SpigotLogger;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractSpigotPlugin extends JavaPlugin implements ImpactorPlugin {

    private final PluginMetadata metadata;
    private final SpigotLogger pluginLogger;

    public AbstractSpigotPlugin(PluginMetadata metadata) {
        this.metadata = metadata;
        this.pluginLogger = new SpigotLogger(this);
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
