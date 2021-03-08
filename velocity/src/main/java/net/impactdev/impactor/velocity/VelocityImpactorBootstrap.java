package net.impactdev.impactor.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.impactdev.impactor.api.logging.Logger;

@Plugin(id = "impactor", name = "Impactor", version = "@version@", authors = {"NickImpact"})
public class VelocityImpactorBootstrap {

    private static VelocityImpactorBootstrap instance;

    private final VelocityImpactorPlugin plugin;
    private final ProxyServer server;

    @Inject
    public VelocityImpactorBootstrap(ProxyServer server) {
        instance = this;
        this.server = server;
        this.plugin = new VelocityImpactorPlugin(this);
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        try {
            this.plugin.init();
        } catch (Exception e) {
            //exception = e;
            e.printStackTrace();
        }
    }

    public static VelocityImpactorBootstrap getInstance() {
        return instance;
    }

    public ProxyServer getProxy() {
        return this.server;
    }

    public Logger getLogger() {
        return this.plugin.getPluginLogger();
    }
}
