package net.impactdev.impactor.launcher;

public interface LaunchablePlugin {

    String path();

    String bootstrapper();

    LauncherBootstrap create(JarInJarClassLoader loader);

}
