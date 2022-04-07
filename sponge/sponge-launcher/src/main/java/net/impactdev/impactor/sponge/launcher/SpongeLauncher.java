package net.impactdev.impactor.sponge.launcher;

import com.google.inject.Inject;
import com.google.inject.Injector;
import net.impactdev.impactor.launcher.ImpactorPluginLauncher;
import net.impactdev.impactor.launcher.JarInJarClassLoader;
import net.impactdev.impactor.launcher.LaunchablePlugin;
import net.impactdev.impactor.launcher.LauncherBootstrap;
import org.spongepowered.api.Server;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.nio.file.Path;
import java.util.function.Supplier;

@Plugin("impactor")
public class SpongeLauncher implements LaunchablePlugin, Supplier<Injector> {

    private static final String INTERNAL_JAR = "impactor-sponge.jarinjar";
    private static final String BOOTSTRAP_CLASS = "net.impactdev.impactor.sponge.SpongeImpactorBootstrap";

    private final LauncherBootstrap plugin;
    private final Injector injector;

    private final LaunchParameters parameters;

    @Inject
    public SpongeLauncher(Injector injector, @ConfigDir(sharedRoot = false) Path configDir) {
        this.injector = injector;
        this.parameters = new LaunchParameters(this, configDir);

        JarInJarClassLoader loader = new JarInJarClassLoader(this.getClass().getClassLoader(), INTERNAL_JAR);
        this.plugin = this.create(loader);

        ImpactorPluginLauncher.initialize(loader);
    }

    @Override
    public Injector get() {
        return this.injector;
    }

    @Listener(order = Order.FIRST)
    public void construct(ConstructPluginEvent event) {
        // TODO - Try/Catch Exceptions and print nicely
        this.plugin.construct();
    }

    @Listener
    public void shutdown(StoppingEngineEvent<Server> event) {
        this.plugin.shutdown();
    }

    @Override
    public String path() {
        return INTERNAL_JAR;
    }

    @Override
    public String bootstrapper() {
        return BOOTSTRAP_CLASS;
    }

    @Override
    public LauncherBootstrap create(JarInJarClassLoader loader) {
        return loader.instantiatePlugin(this.bootstrapper(), LaunchParameters.class, this.parameters);
    }
}
