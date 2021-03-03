package net.impactdev.impactor.common.api;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.registry.Registry;
import net.impactdev.impactor.common.registry.ImpactorRegistry;

public abstract class ImpactorAPIProvider implements Impactor {

    private final Registry registry = new ImpactorRegistry();

    @Override
    public Registry getRegistry() {
        return this.registry;
    }

}
