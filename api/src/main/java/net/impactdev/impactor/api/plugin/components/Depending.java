package net.impactdev.impactor.api.plugin.components;

import net.impactdev.impactor.api.dependencies.Dependency;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.storage.StorageType;

import java.util.Collections;
import java.util.List;

public interface Depending extends ImpactorPlugin {

    default List<Dependency> getAllDependencies() {
        return Collections.emptyList();
    }

    List<StorageType> getStorageRequirements();

}
