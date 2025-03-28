package net.impactdev.impactor.api.economy.launcher;

import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.builtin.ImpactorEconomyService;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ServiceRegistry {

    private static final Map<EconomyService.ServiceDetails, Supplier<EconomyService>> suppliers = new HashMap<>();

    static void register(EconomyService.ServiceDetails details, Supplier<EconomyService> supplier) {
        suppliers.put(details, supplier);
    }

    static EconomyService select(Logger logger, String target) {
        return suppliers.entrySet().stream()
                .filter(entry -> entry.getKey().identifier().equalsIgnoreCase(target))
                .findFirst()
                .map(entry -> entry.getValue().get())
                .orElseGet(() -> {
                    logger.warn("Selected economy service not available, defaulting to Impactor...");

                    return suppliers.get(ImpactorEconomyService.DETAILS).get();
                });
    }
}
