package net.impactdev.impactor.api.economy.launcher;

import com.google.auto.service.AutoService;
import net.impactdev.impactor.api.config.Config;
import net.impactdev.impactor.api.config.ConfigBuilder;
import net.impactdev.impactor.api.config.adapter.MultiConfigurationAdapter;
import net.impactdev.impactor.api.config.adapter.configurate.HoconConfigAdapter;
import net.impactdev.impactor.api.config.adapter.system.EnvironmentVariableConfigAdapter;
import net.impactdev.impactor.api.config.adapter.system.SystemPropertyConfigAdapter;
import net.impactdev.impactor.api.core.ServiceProvider;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.builtin.EconomyConfigKeys;
import net.impactdev.impactor.api.economy.builtin.ImpactorEconomyService;
import net.impactdev.impactor.api.economy.launcher.events.AbstractSuggestEconomyServiceEvent;
import net.impactdev.impactor.api.economy.events.EconomyEvents;
import net.impactdev.impactor.api.economy.events.SuggestEconomyService;
import net.impactdev.impactor.api.platform.events.LifecycleEvents;
import net.impactdev.impactor.loader.Version;
import net.impactdev.impactor.loader.modules.ImpactorModule;
import net.impactdev.impactor.loader.modules.ModuleMetadata;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@AutoService(ImpactorModule.class)
public final class EconomyModule extends ImpactorModule {

    private static final Marker ECONOMY_MARKER = MarkerManager.getMarker("Economy");
    private static final ModuleMetadata METADATA = new ModuleMetadata("Economy", new Version(6, 0, 0));

    @Override
    public ModuleMetadata metadata() {
        return METADATA;
    }

    @Override
    public void initialize() throws Exception {
        LifecycleEvents.SERVER_STARTING.subscribe(starting -> {
            this.logger.info(ECONOMY_MARKER, "Initializing the economy service...");

            final Config config = new ConfigBuilder()
                    .keys(EconomyConfigKeys.class)
                    .adapter(new MultiConfigurationAdapter(List.of(
                            new SystemPropertyConfigAdapter("impactor.economy."), new EnvironmentVariableConfigAdapter("IMPACTOR_ECONOMY_"), new HoconConfigAdapter(
                                    Paths
                                            .get("config")
                                            .resolve("impactor")
                                            .resolve("economy.conf"), () -> this.resource(root -> root
                                    .resolve("configs")
                                    .resolve("economy.conf"))
                            )
                    )))
                    .build();

            // Request suggestions from possible providers
            SuggestEconomyService event = new AbstractSuggestEconomyServiceEvent(ServiceRegistry::register);
            event.suggest(ImpactorEconomyService.DETAILS, () -> new ImpactorEconomyService(config));

            EconomyEvents.SUGGEST_ECONOMY_SERVICE.publish(event);


            final EconomyService selection = ServiceRegistry.select(this.logger, config.get(EconomyConfigKeys.SERVICE_REQUEST));
            ServiceProvider.instance().register(EconomyService.class, selection);

            final EconomyService.ServiceDetails details = selection.details();
            this.logger.info(ECONOMY_MARKER, "Selected {} (Version {})", details.identifier(), details.version());
        });
    }

    public InputStream resource(Function<Path, Path> target) {
        Path path = target.apply(Paths.get("impactor").resolve("assets"));
        return Optional
                .ofNullable(this.getClass().getClassLoader().getResourceAsStream(path.toString().replace("\\", "/")))
                .orElseThrow(() -> new IllegalArgumentException("Target resource not located"));
    }

}
