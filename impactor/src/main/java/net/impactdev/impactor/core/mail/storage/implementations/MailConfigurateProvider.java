package net.impactdev.impactor.core.mail.storage.implementations;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import net.impactdev.impactor.api.mail.MailMessage;
import net.impactdev.impactor.api.mail.Mailbox;
import net.impactdev.impactor.api.storage.connection.configurate.ConfigurateLoader;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.core.mail.ImpactorMailMessage;
import net.impactdev.impactor.core.mail.ImpactorMailbox;
import net.impactdev.impactor.core.mail.storage.MailStorageImplementation;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public final class MailConfigurateProvider implements MailStorageImplementation {

    private final ConfigurateLoader loader;
    private final Path root;
    private final LoadingCache<Path, ReentrantLock> ioLocks;

    public MailConfigurateProvider(@NotNull final ConfigurateLoader loader) {
        this.loader = loader;
        this.root = Paths.get("config").resolve("impactor").resolve("mail").resolve("users");
        this.ioLocks = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(key -> new ReentrantLock());
    }

    private void createDirectoriesIfNotExists(Path path) throws IOException {
        if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
            return;
        }

        Files.createDirectories(path);
    }

    @Override
    public void init() throws Exception {
        this.createDirectoriesIfNotExists(this.root);
    }

    @Override
    public Mailbox fetch(UUID target) throws Exception {
        List<MailMessage> messages = Lists.newArrayList();

        Path path = this.target(target);
        ReentrantLock lock = Objects.requireNonNull(this.ioLocks.get(path));
        lock.lock();
        try {
            for(File file : Objects.requireNonNull(path.toFile().listFiles((d, n) -> n.endsWith(".json")))) {
                this.readMessage(file.toPath()).ifPresent(messages::add);
            }
        } finally {
            lock.unlock();
        }

        return new ImpactorMailbox(messages);
    }

    @Override
    public void save(UUID target, MailMessage message) throws Exception {
        Path path = this.target(target).resolve(message.uuid().toString());

        ReentrantLock lock = Objects.requireNonNull(this.ioLocks.get(path));
        lock.lock();
        try {
            this.writeMessage(path, message);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove(UUID target, UUID message) throws Exception {
        Path path = this.target(target).resolve(message.toString());
        ReentrantLock lock = Objects.requireNonNull(this.ioLocks.get(path));
        lock.lock();
        try {
            this.writeMessage(path, null);
        } finally {
            lock.unlock();
        }
    }

    private Optional<MailMessage> readMessage(Path path) {
        if(path.toFile().exists()) {
            ReentrantLock lock = Objects.requireNonNull(this.ioLocks.get(path));
            lock.lock();
            try {
                UUID uuid = UUID.fromString(path.getFileName().toString().replace(".json", ""));
                ConfigurationNode node = this.loader.loader(path).load();
                UUID sender = node.node("sender").get(UUID.class);
                Instant instant = node.node("timestamp").get(Instant.class);

                JsonElement json = Objects.requireNonNull(node.node("message").get(JsonElement.class));
                Component message = GsonComponentSerializer.gson().deserializeFromTree(json);

                return Optional.of(new ImpactorMailMessage(uuid, sender, message, instant));
            } catch (Exception e) {
                ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
                return Optional.empty();
            } finally {
                lock.unlock();
            }
        }

        return Optional.empty();
    }

    private void writeMessage(Path target, @Nullable MailMessage message) {
        ReentrantLock lock = Objects.requireNonNull(this.ioLocks.get(target));
        lock.lock();
        try {
            if(message == null) {
                Files.deleteIfExists(target);
            } else {
                JsonElement json = GsonComponentSerializer.gson().serializeToTree(message.content());

                ConfigurationLoader<?> loader = this.loader.loader(target);
                ConfigurationNode node = loader.createNode();
                node.set(UUID.class, message.sender());
                node.set(Instant.class, message.timestamp());
                node.set(JsonElement.class, json);

                loader.save(node);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            lock.unlock();
        }
    }

    private Path target(UUID target) throws Exception {
        Path path = this.root.resolve(target.toString().substring(0, 2)).resolve(target.toString());
        this.createDirectoriesIfNotExists(path);

        return path;
    }
}
