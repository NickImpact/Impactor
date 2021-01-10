package net.impactdev.impactor.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.impactdev.impactor.common.api.ApiRegistrationUtil;
import net.impactdev.impactor.velocity.api.VelocityImpactorAPIProvider;
import net.impactdev.impactor.velocity.logging.VelocityLogger;
import net.impactdev.impactor.velocity.plugin.AbstractVelocityPlugin;
import net.impactdev.impactor.velocity.scheduler.VelocitySchedulerAdapter;
import org.slf4j.Logger;

@Plugin(id = "impactor", name = "Impactor", version = "@version@", authors = {"NickImpact"})
public class VelocityImpactorBootstrap extends AbstractVelocityPlugin {

    private static VelocityImpactorBootstrap instance;

    private final ProxyServer server;
    private final VelocityLogger logger;

    @Inject
    public VelocityImpactorBootstrap(ProxyServer server, Logger fallback) {
        super(metadata, logger);
        instance = this;
        this.server = server;
        this.logger = new VelocityLogger(this);
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        ApiRegistrationUtil.register(new VelocityImpactorAPIProvider(new VelocitySchedulerAdapter(this)));
    }

    public static VelocityImpactorBootstrap getInstance() {
        return instance;
    }

    public ProxyServer getProxy() {
        return this.server;
    }

    public VelocityLogger getFallbackLogger() {
        return this.logger;
    }
}
