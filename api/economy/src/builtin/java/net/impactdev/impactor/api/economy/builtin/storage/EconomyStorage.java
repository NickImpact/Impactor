package net.impactdev.impactor.api.economy.builtin.storage;

import net.impactdev.impactor.api.core.logging.ExceptionPrinter;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.storage.Storage;
import net.impactdev.impactor.api.storage.StorageMetadata;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class EconomyStorage implements Storage {

    private final EconomyStorageImplementation implementation;

    public EconomyStorage(final EconomyStorageImplementation implementation) {
        this.implementation = implementation;
    }

    @Override
    public void init() throws Exception {
        this.implementation.init();
    }

    @Override
    public void shutdown() throws Exception {
        this.implementation.shutdown();
    }

    @Override
    public CompletableFuture<StorageMetadata> metadata() throws Exception {
        return supply(this.implementation::metadata);
    }

    public CompletableFuture<Boolean> exists(UUID uuid, Currency currency) {
        return supply(() -> this.implementation.exists(uuid, currency));
    }

    public CompletableFuture<Account> getOrCreate(UUID uuid, Currency currency) {
        return supply(() -> this.implementation.account(uuid, currency));
    }

    public CompletableFuture<Void> delete(UUID uuid, Currency currency) {
        return run(() -> this.implementation.delete(uuid, currency));
    }

    public CompletableFuture<Set<Account>> performing(Currency currency, int limit) {
        return supply(() -> this.implementation.performing(currency, limit));
    }

    private static CompletableFuture<Void> run(ThrowingRunnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                ExceptionPrinter.print(e);

                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }

                throw new CompletionException(e);
            }
        });
    }

    private static <T> CompletableFuture<T> supply(ThrowingSupplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                ExceptionPrinter.print(e);

                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }

                throw new CompletionException(e);
            }
        });
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {

        T get() throws Exception;

    }

}
