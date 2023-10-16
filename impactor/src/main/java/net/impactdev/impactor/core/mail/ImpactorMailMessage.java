package net.impactdev.impactor.core.mail;

import net.impactdev.impactor.api.mail.MailMessage;
import net.kyori.adventure.text.Component;

import java.time.Instant;
import java.util.UUID;

public class ImpactorMailMessage implements MailMessage {

    private final UUID uuid;
    private final Component message;
    private final Instant timestamp;
    private final UUID sender;

    public ImpactorMailMessage(UUID sender, Component message) {
        this.uuid = UUID.randomUUID();
        this.message = message;
        this.sender = sender;
        this.timestamp = Instant.now();
    }

    public ImpactorMailMessage(UUID uuid, UUID sender, Component message, Instant timestamp) {
        this.uuid = uuid;
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    @Override
    public Component content() {
        return this.message;
    }

    @Override
    public Instant timestamp() {
        return this.timestamp;
    }

    @Override
    public UUID uuid() {
        return this.uuid;
    }

    @Override
    public UUID sender() {
        return this.sender;
    }

}
