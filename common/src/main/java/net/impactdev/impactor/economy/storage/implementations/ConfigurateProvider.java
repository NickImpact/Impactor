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

package net.impactdev.impactor.economy.storage.implementations;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.services.economy.EconomyService;
import net.impactdev.impactor.api.services.economy.accounts.Account;
import net.impactdev.impactor.api.services.economy.accounts.AccountAccessor;
import net.impactdev.impactor.api.services.economy.currency.Currency;
import net.impactdev.impactor.api.storage.connection.configurate.ConfigurateLoader;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.economy.ImpactorEconomyService;
import net.impactdev.impactor.economy.accounts.ImpactorAccount;
import net.impactdev.impactor.economy.accounts.accessors.UniqueAccountAccessor;
import net.impactdev.impactor.economy.accounts.links.UniqueImpactorAccountLinker;
import net.impactdev.impactor.economy.accounts.translators.AccountTranslator;
import net.impactdev.impactor.economy.storage.EconomyStorage;
import net.impactdev.impactor.economy.storage.EconomyStorageImplementation;
import net.impactdev.json.JObject;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurateProvider implements EconomyStorageImplementation {

    private final ConfigurateLoader loader;
    private final Path root;

    private final LoadingCache<Path, ReentrantLock> ioLocks;

    public ConfigurateProvider(@NotNull final ConfigurateLoader loader) {
        this.loader = loader;
        this.root = Paths.get("impactor").resolve("economy");
        this.ioLocks = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(key -> new ReentrantLock());
    }

    @Override
    public String name() {
        return "Configurate - " + this.loader.name();
    }

    @Override
    public void init() throws Exception {
        this.createDirectoriesIfNotExists(Group.USERS.root(this.root));
        this.createDirectoriesIfNotExists(Group.VIRTUAL.root(this.root));
    }

    @Override
    public void shutdown() throws Exception {}

    @Override
    public PrettyPrinter.IPrettyPrintable meta() {
        return printer -> {
            printer.add("File Counts:");
            long totalSize = 0;
            for(Group group : Group.values()) {
                try (Stream<Path> files = Files.walk(group.transformer.apply(this.root))) {
                    long count = files.filter(path -> Files.isRegularFile(path) && path.endsWith(".conf")).count();
                    printer.add("  %s: %d (%s)", group.name(), count, totalSize += this.size(files));
                } catch (Exception e) {
                    printer.add("  %s: Unknown", group.name());
                }
            }
            printer.newline().add("Total Used Space: %d", totalSize);
        };
    }

    private void createDirectoriesIfNotExists(Path path) throws IOException {
        if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
            return;
        }

        Files.createDirectories(path);
    }

    @Override
    public Account account(UUID uuid, Currency currency) throws Exception {
        Path target = Group.USERS.root(this.root)
                .resolve(uuid.toString().substring(0, 2))
                .resolve(uuid.toString())
                .resolve(PlainTextComponentSerializer.plainText().serialize(currency.name()) + ".conf");

        if(Files.exists(target)) {
            ConfigurationNode node = this.read(target);
            JObject json = new JObject().add("balance", node.node("balance").getDouble());

            return AccountTranslator.deserialize(currency, json.toJson(), this.createUniqueLinker(uuid));
        } else {
            Account account = ImpactorAccount.create(currency, this.createUniqueLinker(uuid));
            this.saveAccount(uuid, account);

            return account;
        }
    }

    @Override
    public boolean saveAccount(UUID uuid, Account account) throws Exception {
        Path target = Group.USERS.root(this.root)
                .resolve(uuid.toString().substring(0, 2))
                .resolve(uuid.toString())
                .resolve(PlainTextComponentSerializer.plainText().serialize(account.currency().name()) + ".conf");

        this.saveOrDelete(target, account);
        return true;
    }

    @Override
    public List<AccountAccessor> accessors() throws Exception {
        List<AccountAccessor> results = Lists.newArrayList();

        Path base = Group.USERS.root(this.root);
        try(Stream<Path> paths = Files.walk(base, 1).filter(in -> !base.equals(in))) {
            for(Path parent : paths.collect(Collectors.toSet())) {
                try(Stream<Path> uuids = Files.walk(parent, 1).filter(in -> !parent.equals(in))) {
                    uuids.forEach(uuid -> results.add(new UniqueAccountAccessor(UUID.fromString(uuid.getFileName().toString()))));
                }
            }
        }

        return results;
    }

    @Override
    public boolean purge() throws Exception {
        FileUtils.cleanDirectory(this.root.toFile());
        return true;
    }

    private enum Group {
        USERS(root -> root.resolve("users")),
        VIRTUAL(root -> root.resolve("virtual"));

        private final Function<Path, Path> transformer;

        Group(Function<Path, Path> transformer) {
            this.transformer = transformer;
        }

        public Path root(Path parent) {
            return this.transformer.apply(parent);
        }
    }

    private long size(Stream<Path> paths) throws IOException {
        return paths.filter(Files::isRegularFile).mapToLong(this::sizeCatching).sum();
    }

    private long sizeCatching(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            return 0L;
        }
    }

    @NotNull
    private ConfigurationNode read(Path target) throws IOException {
        ReentrantLock lock = Objects.requireNonNull(this.ioLocks.get(target));
        lock.lock();
        try {
            if(!target.toFile().exists()) {
                throw new FileNotFoundException("Target file was not found");
            }

            return this.loader.loader(target).load();
        } finally {
            lock.unlock();
        }
    }

    private void saveOrDelete(Path target, @Nullable Account account) throws IOException {
        this.createDirectoriesIfNotExists(target.getParent());
        ReentrantLock lock = Objects.requireNonNull(this.ioLocks.get(target));
        lock.lock();

        try {
            if(account == null) {
                Files.deleteIfExists(target);
                return;
            }

            ConfigurationNode node = BasicConfigurationNode.root();
            node.node("balance").set(account.balance());

            this.loader.loader(target).save(node);
        } finally {
            lock.unlock();
        }
    }

    private UniqueImpactorAccountLinker createUniqueLinker(UUID uuid) {
        EconomyStorage parent = ((ImpactorEconomyService) Impactor.instance().services().provide(EconomyService.class)).storage();
        return new UniqueImpactorAccountLinker(uuid, parent);
    }
}
