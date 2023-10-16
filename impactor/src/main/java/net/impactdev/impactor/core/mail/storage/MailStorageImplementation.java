package net.impactdev.impactor.core.mail.storage;

import net.impactdev.impactor.api.mail.MailMessage;
import net.impactdev.impactor.api.mail.Mailbox;
import net.impactdev.impactor.api.storage.connection.StorageConnection;

import java.util.UUID;

public interface MailStorageImplementation extends StorageConnection {

    Mailbox fetch(UUID target) throws Exception;

    void save(UUID target, MailMessage message) throws Exception;

    void remove(UUID target, UUID message) throws Exception;

}
