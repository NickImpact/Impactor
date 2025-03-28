package net.impactdev.impactor.api.commands.launcher;

import com.google.auto.service.AutoService;
import net.impactdev.impactor.loader.Version;
import net.impactdev.impactor.loader.modules.ImpactorModule;
import net.impactdev.impactor.loader.modules.ModuleMetadata;

@AutoService(ImpactorModule.class)
public final class CommandsModule extends ImpactorModule {

    private static final ModuleMetadata METADATA = new ModuleMetadata(
            "Commands",
            new Version(6, 0, 0)
    );

    @Override
    public ModuleMetadata metadata() {
        return METADATA;
    }

    @Override
    public void initialize() throws Exception {

    }
}
