/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.core.mail.storage.implementations;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import net.impactdev.impactor.api.mail.MailMessage;
import net.impactdev.impactor.api.mail.filters.MailFilter;
import net.impactdev.impactor.api.storage.connection.configurate.ConfigurateLoader;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.mail.ImpactorMailMessage;
import net.impactdev.impactor.core.mail.storage.MailStorageImplementation;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.util.TriState;
import org.apache.commons.io.FileUtils;
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
import java.util.stream.Stream;

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
    public String name() {
        return null;
    }

    @Override
    public void init() throws Exception {
        this.createDirectoriesIfNotExists(this.root);
    }

    @Override
    public void shutdown() throws Exception {

    }

    @Override
    public void meta(PrettyPrinter printer) throws Exception {

    }

    @Override
    public List<MailMessage> mail(UUID target) throws Exception {
        List<MailMessage> inbox = Lists.newArrayList();

        Path branch = this.target(target);
        for(File file : Objects.requireNonNull(branch.toFile().listFiles())) {
            if(file.isDirectory()) {
                for(File child : Objects.requireNonNull(file.listFiles())) {
                    this.readMessage(child.toPath()).ifPresent(inbox::add);
                }
            }
        }

        return inbox;
    }

    @Override
    public boolean append(UUID target, MailMessage message) throws Exception {
        String uuid = message.uuid().toString();
        Path branch = this.target(target).resolve(uuid.substring(0, 2));
        Files.createDirectories(branch);

        this.writeMessage(branch.resolve(uuid + ".json"), message);
        return true;
    }

    @Override
    public TriState delete(UUID target, MailMessage message) throws Exception {
        String uuid = message.uuid().toString();
        File root = this.target(target).toFile();
        if(root.isDirectory() && root.list() == null) {
            return TriState.NOT_SET;
        }

        Path leaf = root.toPath().resolve(uuid.substring(0, 2)).resolve(uuid + ".json");
        if(Files.exists(leaf)) {
            this.writeMessage(leaf, null);
            return TriState.TRUE;
        }

        return TriState.FALSE;
    }

    @Override
    public TriState deleteWhere(@NotNull UUID target, @Nullable MailFilter filter) throws Exception {
        Path root = this.target(target);
        if(filter == null && Files.exists(root)) {
            FileUtils.deleteDirectory(root.toFile());
            return TriState.TRUE;
        } else if(filter == null) {
            return TriState.NOT_SET;
        }

        boolean failed = false;
        Stream<MailMessage> mail = this.mail(target).stream().filter(filter);
        for(MailMessage message : mail.toList()) {
            TriState result = this.delete(target, message);
            if(result == TriState.NOT_SET) {
                throw new IllegalStateException("Inbox empty despite recent successful mail fetch");
            }

            if(!failed && result == TriState.FALSE) {
                failed = true;
            }
        }

        return TriState.byBoolean(!failed);
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
                if(message.source().isPresent()) {
                    node.set(UUID.class, message.source().get());
                }
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
        String id = target.toString();
        Path path = this.root.resolve(id.substring(0, 2)).resolve(id);
        this.createDirectoriesIfNotExists(path);

        return path;
    }
}
