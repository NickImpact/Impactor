package net.impactdev.impactor.sponge.launcher;

import com.google.inject.Injector;

import java.nio.file.Path;
import java.util.function.Supplier;

public class LaunchParameters {

    private final Supplier<Injector> loader;
    private final Path configDir;

    public LaunchParameters(final Supplier<Injector> loader, final Path configDir) {
        this.loader = loader;
        this.configDir = configDir;
    }

    public Supplier<Injector> loader() {
        return loader;
    }

    public Path configDirectory() {
        return configDir;
    }
}
