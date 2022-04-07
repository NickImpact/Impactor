package net.impactdev.impactor.launcher;

import java.net.URL;

public class ImpactorPluginLauncher {

    private static ImpactorPluginLauncher launcher;
    private final JarInJarClassLoader loader;

    public static ImpactorPluginLauncher initialize(JarInJarClassLoader loader) {
        if(launcher != null) {
            throw new LoadingException("Plugin Launcher already initialized");
        }

        return new ImpactorPluginLauncher(loader);
    }

    public static ImpactorPluginLauncher get() {
        return launcher;
    }

    private ImpactorPluginLauncher(JarInJarClassLoader loader) {
        launcher = this;
        this.loader = loader;
    }

    public LauncherBootstrap bootstrap(ClassLoader loader, LaunchablePlugin plugin) {
        URL url = JarInJarClassLoader.extractJar(loader, plugin.path());
        this.loader.addJarToClasspath(url);

        return plugin.create(this.loader);
    }

}
