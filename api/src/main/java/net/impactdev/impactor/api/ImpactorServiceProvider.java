package net.impactdev.impactor.api;

import org.checkerframework.checker.nullness.qual.NonNull;

public final class ImpactorServiceProvider {

    private static Impactor instance;

    public static @NonNull Impactor get() {
        if(instance == null) {
            throw new IllegalStateException("The Impactor API is not loaded");
        }

        return instance;
    }

    static void register(Impactor service) {
        instance = service;
    }

    static void unregister() {
        instance = null;
    }

    private ImpactorServiceProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
