package com.nickimpact.impactor.common.api;

import com.nickimpact.impactor.api.Impactor;
import com.nickimpact.impactor.api.registry.Registry;
import com.nickimpact.impactor.common.registry.ImpactorRegistry;

public abstract class ImpactorAPIProvider implements Impactor {

    private final Registry registry = new ImpactorRegistry();

    @Override
    public Registry getRegistry() {
        return this.registry;
    }

}
