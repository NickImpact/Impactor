package net.impactdev.impactor.api.economy.events;

import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.launcher.events.AbstractSuggestEconomyServiceEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.eventgen.annotations.ImplementedBy;

import java.util.function.Supplier;

/**
 * Provides a means of supplying a custom economy service that a user might be able to use for
 * their desires. Mods/Plugins which wish to have custom implementations should provide such via
 * {@link #suggest(EconomyService.ServiceDetails, Supplier)}, making sure to supply the source
 * identifier as desired. This identifier is what the end user will use to identify your service
 * for selection.
 *
 * <p>As your service might not be selected, the suggestion is to be provided via a {@link Supplier}.
 * This helps ensure critical resources your service might require are not initialized and then
 * forgotten in memory. However, for this to be properly effective, you should ensure the actual
 * creation of the service is within the supplier.</p>
 *
 * @since 6.0.0
 */
@ImplementedBy(AbstractSuggestEconomyServiceEvent.class)
public interface SuggestEconomyService {

    /**
     * Suggests an economy service using the given identifier. As your service might not be selected, the service
     * is bound through a supplier responsible for actually creating the service.
     *
     * @param details  Details on the resulting service, if selected
     * @param supplier The supplier responsible for creating the service
     * @since 6.0.0
     */
    void suggest(final @NotNull EconomyService.ServiceDetails details, final @NotNull Supplier<@NotNull EconomyService> supplier);

}
