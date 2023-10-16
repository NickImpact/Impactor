package net.impactdev.impactor.core.mail;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.mail.MailMessage;
import net.impactdev.impactor.api.mail.Mailbox;
import net.impactdev.impactor.api.mail.filters.MailFilter;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ImpactorMailbox implements Mailbox {

    private final List<MailMessage> messages = Lists.newCopyOnWriteArrayList();

    public ImpactorMailbox(List<MailMessage> messages) {
        this.messages.addAll(messages);
    }

    @Override
    public Set<MailMessage> mail(MailFilter... filters) {
        Stream<MailMessage> stream = this.messages.stream();
        for(MailFilter filter : filters) {
            stream = stream.filter(filter);
        }

        return stream.collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public CompletableFuture<Boolean> append(MailMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            this.messages.add(message);

            return true;
        }).orTimeout(5, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Boolean> remove(MailMessage message) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> removeIf(MailFilter... filters) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> clear() {
        return CompletableFuture.completedFuture(false);
    }

}
