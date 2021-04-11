package net.impactdev.impactor.sponge.util;

import net.impactdev.impactor.api.dependencies.classloader.ReflectionClassLoader;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;

import java.lang.reflect.Field;
import java.net.URLClassLoader;

public class SpongeClassLoader extends ReflectionClassLoader {

    private static URLClassLoader extractClassLoader(Object in) {
        ClassLoader classLoader = in.getClass().getClassLoader();

        // try to cast directly to URLClassLoader in case things change in the future
        if (classLoader instanceof URLClassLoader) {
            return castToUrlClassLoader(classLoader);
        }

        Class<? extends ClassLoader> classLoaderClass = classLoader.getClass();

        if (!classLoaderClass.getName().equals("cpw.mods.modlauncher.TransformingClassLoader")) {
            throw new IllegalStateException("ClassLoader is not instance of TransformingClassLoader: " + classLoaderClass.getName());
        }

        try {
            Field delegatedClassLoaderField = classLoaderClass.getDeclaredField("delegatedClassLoader");
            delegatedClassLoaderField.setAccessible(true);
            Object delegatedClassLoader = delegatedClassLoaderField.get(classLoader);
            return castToUrlClassLoader(delegatedClassLoader);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public SpongeClassLoader(ImpactorPlugin bootstrap) throws IllegalStateException {
        super(bootstrap, SpongeClassLoader::extractClassLoader);
    }

}
