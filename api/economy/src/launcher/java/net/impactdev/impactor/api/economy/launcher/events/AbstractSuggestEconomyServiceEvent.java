package net.impactdev.impactor.api.economy.launcher.events;

import com.google.common.base.Preconditions;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.events.SuggestEconomyService;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class AbstractSuggestEconomyServiceEvent implements SuggestEconomyService {

    private final ServiceSuggestor suggestor;

    public AbstractSuggestEconomyServiceEvent(ServiceSuggestor suggestor) {
        this.suggestor = suggestor;
    }

    @Override
    public void suggest(@NotNull EconomyService.ServiceDetails details, @NotNull Supplier<@NotNull EconomyService> supplier) {
        Preconditions.checkNotNull(details, "Service details cannot be null");
        Preconditions.checkNotNull(supplier, "Supplier cannot be null");

        this.suggestor.suggest(details, supplier);
    }

    @FunctionalInterface
    public interface ServiceSuggestor {

        void suggest(@NotNull EconomyService.ServiceDetails details, @NotNull Supplier<@NotNull EconomyService> supplier);

    }
}
