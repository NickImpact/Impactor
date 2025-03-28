package net.impactdev.impactor.api.economy.builtin.networking;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.impactdev.impactor.api.core.logging.ExceptionPrinter;
import net.impactdev.impactor.api.economy.builtin.networking.messages.TransactionRequestMessage;
import net.impactdev.impactor.api.economy.builtin.networking.messages.TransactionResponseMessage;
import net.impactdev.impactor.api.economy.builtin.transactions.TransactionRequest;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.events.EventSubscriber;
import net.impactdev.impactor.api.events.EventSubscription;
import net.impactdev.impactor.api.networking.messages.Message;
import net.impactdev.impactor.api.networking.messages.MessageConsumer;
import net.impactdev.impactor.api.networking.messages.MessageRegistry;
import net.impactdev.impactor.api.networking.NetworkingService;
import net.impactdev.impactor.api.networking.Messenger;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public final class EconomyNetworkingService implements NetworkingService {

    private static final ExecutorService NETWORKING = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
            .setNameFormat("Impactor - Economy Networking (%d)")
            .setUncaughtExceptionHandler((thread, exception) -> {
                ExceptionPrinter.print(exception);
            })
            .setDaemon(true)
            .build());

    private final Logger logger;
    private final Messenger messenger;
    private final MessageConsumer consumer;
    private final MessageRegistry registry;

    public EconomyNetworkingService(final Logger logger, final Messenger.Provider messenger) {
        this.logger = logger;
        this.consumer = new EconomyMessageConsumer(this.logger, this);
        this.registry = new EconomyMessageRegistry(logger);

        this.messenger = messenger.obtain(this.consumer);
    }

    private void initialize() {
        this.registry.register(TransactionRequestMessage.KEY, TransactionRequestMessage.CODEC);
        this.registry.register(TransactionResponseMessage.KEY, TransactionResponseMessage.CODEC);
    }

    @Override
    public Messenger messenger() {
        return this.messenger;
    }

    @Override
    public MessageConsumer consumer() {
        return this.consumer;
    }

    @Override
    public MessageRegistry registry() {
        return this.registry;
    }

    public <M extends Message> EventSubscription<M> subscribe(EventSubscriber<M> subscriber) {
        return null;
    }

    public CompletableFuture<EconomyTransaction> publish(final TransactionRequest transaction) {
        final EconomyMessageConsumer consumer = (EconomyMessageConsumer) this.consumer;
        final TransactionRequestMessage message = new TransactionRequestMessage(consumer.generateForOutgoing(), transaction);

        final AtomicReference<TransactionResponseMessage> watcher = new AtomicReference<>();
        return CompletableFuture.supplyAsync(
                () -> {
                    this.logger.debug("Publishing transaction request with ID: {}", message.id());
                    this.messenger.publish(message);

                    while (watcher.get() == null) {
                        try {
                            Thread.sleep(50);
                        } catch (final InterruptedException e) {
                            ExceptionPrinter.print(e);
                        }
                    }

                    return watcher.get().transaction();
                }, NETWORKING
        );
    }

    @Override
    public void shutdown() {
        this.logger.info("Shutting down networking service...");
        this.messenger.shutdown();
    }

}
