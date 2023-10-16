package net.impactdev.impactor.core.mail.storage;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.mail.MailMessage;
import net.impactdev.impactor.api.mail.Mailbox;
import net.impactdev.impactor.api.storage.Storage;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.utility.future.ThrowingRunnable;
import net.impactdev.impactor.core.utility.future.ThrowingSupplier;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public final class MailStorage implements Storage {

    private final MailStorageImplementation implementation;
    private final AsyncLoadingCache<UUID, Mailbox> mailboxes;

    MailStorage(MailStorageImplementation implementation) {
        this.implementation = implementation;
        this.mailboxes = Caffeine.newBuilder()
                .expireAfterAccess(1, TimeUnit.HOURS)
                .buildAsync(this.implementation::fetch);
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
    public CompletableFuture<Void> meta(PrettyPrinter printer) {
        return run(() -> this.implementation.meta(printer)).orTimeout(5, TimeUnit.SECONDS);
    }

    public CompletableFuture<Mailbox> fetch(UUID target) {
        return this.mailboxes.get(target);
    }

    public CompletableFuture<Void> save(UUID target, MailMessage message) {
        return run(() -> this.implementation.save(target, message)).thenAccept(ignore -> this.mailboxes.synchronous().invalidate(target));
    }

    public CompletableFuture<Void> remove(UUID target, UUID message) {
        return run(() -> this.implementation.remove(target, message)).thenAccept(ignore -> this.mailboxes.synchronous().invalidate(target));
    }

    private static CompletableFuture<Void> run(ThrowingRunnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, Impactor.instance().scheduler().async());
    }

    private static <T> CompletableFuture<T> supply(ThrowingSupplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.supply();
            } catch (Exception e) {
                ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, Impactor.instance().scheduler().async());
    }
}
