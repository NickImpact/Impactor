package net.impactdev.impactor.core.mail;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.mail.MailMessage;
import net.impactdev.impactor.api.mail.MailService;
import net.impactdev.impactor.api.mail.Mailbox;
import net.impactdev.impactor.api.storage.StorageType;
import net.impactdev.impactor.core.economy.EconomyConfig;
import net.impactdev.impactor.core.mail.storage.MailStorage;
import net.impactdev.impactor.core.mail.storage.MailStorageFactory;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.kyori.adventure.text.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ImpactorMailService implements MailService {

    private final Config config;
    private final MailStorage storage;

    ImpactorMailService() {
        this.config = Config.builder()
                .path(BaseImpactorPlugin.instance().configurationDirectory().resolve("mail.conf"))
                .provider(EconomyConfig.class)
                .provideIfMissing(() -> BaseImpactorPlugin.instance().resource(root -> root.resolve("configs").resolve("mail.conf")))
                .build();

        this.storage = MailStorageFactory.instance(
                BaseImpactorPlugin.instance(),
                this.config.get(MailConfig.STORAGE_TYPE),
                StorageType.JSON
        );
    }

    @Override
    public CompletableFuture<Mailbox> mailbox(UUID target) {
        return this.storage.fetch(target);
    }

    @Override
    public CompletableFuture<Void> send(UUID from, UUID to, Component message) {
        return this.mailbox(to).thenAccept(mailbox -> {
            MailMessage mail = new ImpactorMailMessage(from, message);
            mailbox.append(mail);
        });

    }

    @Override
    public String name() {
        return "Impactor Mail Service";
    }
}
