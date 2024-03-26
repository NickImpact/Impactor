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

package net.impactdev.impactor.core.economy.storage.implementations;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.currency.CurrencyProvider;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.details.EconomyResultType;
import net.impactdev.impactor.api.economy.transactions.details.EconomyTransactionType;
import net.impactdev.impactor.api.platform.players.PlatformPlayerService;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.storage.connection.configurate.ConfigurateLoader;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.economy.accounts.ImpactorAccount;
import net.impactdev.impactor.core.economy.storage.EconomyStorageImplementation;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.kyori.adventure.key.Key;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ConfigurateProvider implements EconomyStorageImplementation {

    private final ConfigurateLoader loader;
    private final Path root;

    private enum Group {
        Users("users"),
        Virtual("virtual");

        private final String directory;

        Group(String directory) {
            this.directory = directory;
        }

        private Path transform(Path parent) {
            return parent.resolve(this.directory);
        }
    }

    private final LoadingCache<Path, ReentrantLock> ioLocks;

    public ConfigurateProvider(@NotNull final ConfigurateLoader loader) {
        this.loader = loader;
        this.root = Paths.get("config").resolve("impactor").resolve("economy");
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
        Path accounts = this.root.resolve("accounts");
        this.createDirectoriesIfNotExists(Group.Users.transform(accounts));
        this.createDirectoriesIfNotExists(Group.Virtual.transform(accounts));
    }

    @Override
    public void shutdown() throws Exception {}

    @Override
    public void meta(PrettyPrinter printer) throws Exception {
        printer.add("File Counts:");
        long totalSize = 0;
//        for(Group group : Group.values()) {
//            try (Stream<Path> files = Files.walk(group.transformer.apply(this.root))) {
//                long count = files.filter(path -> Files.isRegularFile(path) && path.endsWith(".conf")).count();
//                printer.add("  %s: %d (%s)", group.name(), count, totalSize += this.size(files));
//            } catch (Exception e) {
//                printer.add("  %s: Unknown", group.name());
//            }
//        }
        printer.newline().add("Total Used Space: %d", totalSize);
    }

    private void createDirectoriesIfNotExists(Path path) throws IOException {
        if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
            return;
        }

        Files.createDirectories(path);
    }

    @Override
    public boolean hasAccount(Currency currency, UUID uuid) throws Exception {
        for(Group group : Group.values()) {
            Path target = group.transform(this.root.resolve("accounts"))
                    .resolve(uuid.toString().substring(0, 2))
                    .resolve(uuid + ".conf");

            if(Files.exists(target)) {
                ConfigurationNode node = this.read(target).node(currency.key().asString());
                return !node.virtual();
            }
        }

        return false;
    }

    @Override
    public Account account(Currency currency, UUID uuid, Account.AccountModifier modifier) throws Exception {
        for(Group group : Group.values()) {
            Path target = group.transform(this.root.resolve("accounts"))
                    .resolve(uuid.toString().substring(0, 2))
                    .resolve(uuid + ".conf");

            if(Files.exists(target)) {
                ConfigurationNode node = this.read(target).node(currency.key().asString());
                if(!node.virtual()) {
                    return ImpactorAccount.load(currency, uuid, group == Group.Virtual, BigDecimal.valueOf(node.getDouble()));
                }

                ImpactorAccount.ImpactorAccountBuilder builder = new ImpactorAccount.ImpactorAccountBuilder();
                builder.currency(currency).owner(uuid);
                modifier.modify(builder);

                if(group == Group.Users) {
                    builder.overrideVirtuality(false);
                }

                Account account = builder.build();
                this.save(account);

                return account;
            }
        }

        Account.AccountBuilder builder = new ImpactorAccount.ImpactorAccountBuilder();
        builder.currency(currency).owner(uuid);
        builder = modifier.modify(builder);

        PlatformPlayerService service = Impactor.instance().services().provide(PlatformPlayerService.class);
        if(PlatformSource.SERVER_UUID.equals(uuid)) {
            builder.virtual();
        }

        // TODO - Validate against a user cache or UUID lookup if the target UUID is a player

        Account account = builder.build();
        this.save(account);

        return account;
    }

    @Override
    public void save(Account account) throws Exception {
        Path accounts = this.root.resolve("accounts");
        Path target = (account.virtual() ? Group.Virtual.transform(accounts) : Group.Users.transform(accounts))
                .resolve(account.owner().toString().substring(0, 2))
                .resolve(account.owner() + ".conf");

        this.save(target, account);
    }

    @Override
    public void accounts(Multimap<Currency, Account> cache) throws Exception {
        Path root = this.root.resolve("accounts");

        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        CurrencyProvider currencies = service.currencies();
        try(Stream<Path> files = Files.walk(root)) {
            files.filter(path -> path.getFileName().toString().endsWith(".conf"))
                    .forEach(path -> {
                        try {
                            boolean virtual = path.getParent().getParent().getFileName().toString().equals("virtual");
                            String name = path.getFileName().toString();
                            UUID owner = UUID.fromString(name.substring(0, name.indexOf(".")));

                            ConfigurationNode data = this.read(path);
                            for(Object key : data.childrenMap().keySet()) {
                                Optional<Currency> currency = currencies.currency(Key.key((String) key));
                                if(currency.isPresent()) {
                                    Collection<Account> registered = cache.get(currency.get());
                                    if(registered.stream().noneMatch(account -> account.owner().equals(owner))) {
                                        Account account = ImpactorAccount.load(currency.get(), owner, virtual, BigDecimal.valueOf(data.node(key).getDouble()));
                                        cache.put(account.currency(), account);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            BaseImpactorPlugin.instance().logger().severe("Economy: Failed to read account file: " + path.getFileName());
                            e.printStackTrace();
                        }
                    });
        }
    }

    @Override
    public void delete(Currency currency, UUID uuid) throws Exception {
        for(Group group : Group.values()) {
            Path target = group.transform(this.root.resolve("accounts"))
                    .resolve(uuid.toString().substring(0, 2))
                    .resolve(uuid + ".conf");

            if(Files.exists(target)) {
                ConfigurationNode root = this.read(target);
                ConfigurationNode data = root.node(currency.key().asString());
                if (!data.virtual()) {
                    if (!root.removeChild(data.key())) {
                        throw new IllegalStateException("Failed to delete account...");
                    }

                    this.loader.loader(target).save(root);
                }

                if (root.empty()) {
                    Files.delete(target);
                }

                return;
            }
        }
    }

    @Override
    public void logTransaction(EconomyTransaction transaction) throws Exception {
        Account target = transaction.account();
        Path accounts = this.root.resolve("accounts");
        Path transactions = (target.virtual() ? Group.Virtual.transform(accounts) : Group.Users.transform(accounts))
                .resolve(target.owner().toString().substring(0, 2))
                .resolve("transactions");

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
        Path branch = transactions.resolve(date.format(transaction.timestamp()));
        Path leaf = branch.resolve("transactions.log");

        this.createDirectoriesIfNotExists(leaf.getParent());
        ReentrantLock lock = Objects.requireNonNull(this.ioLocks.get(leaf));
        lock.lock();

        try {
            ConfigurationNode node;
            if(Files.exists(leaf)) {
                node = this.loader.loader(leaf).load();
            } else {
                node = BasicConfigurationNode.root();
            }

            int id = node.node("transactions").getInt(0) + 1;
            node.node("transactions").set(id);
            node.node("" + id).node("currency").set(transaction.currency().key().asString());
            node.node("" + id).node("type").set(transaction.type().name());
            node.node("" + id).node("result").set(transaction.result().name());
            node.node("" + id).node("amount").set(transaction.amount().doubleValue());
            node.node("" + id).node("timestamp").set(transaction.timestamp());

            this.loader.loader(leaf).save(node);
        } catch (Exception e) {
            ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void sync(Account account, Instant since) throws Exception {
        Path accounts = this.root.resolve("accounts");
        Path transactions = (account.virtual() ? Group.Virtual.transform(accounts) : Group.Users.transform(accounts))
                .resolve(account.owner().toString().substring(0, 2))
                .resolve("transactions");

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
        Files.walk(transactions)
                .filter(Files::isRegularFile)
                .filter(path -> {
                    System.out.println("Reading path: " + path);
                    String name = path.getParent().getFileName().toString();
                    LocalDate timestamp = LocalDate.parse(name, date);

                    LocalDate compare = LocalDate.ofInstant(since, ZoneId.systemDefault());
                    return compare.isBefore(timestamp) || compare.isEqual(timestamp);
                })
                .map(path -> {
                    ReentrantLock lock = Objects.requireNonNull(this.ioLocks.get(path));
                    lock.lock();
                    try {
                        return this.loader.loader(path).load();
                    } catch (Exception e) {
                        ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
                        return null;
                    } finally {
                        lock.unlock();
                    }
                })
                .filter(Objects::nonNull)
                .map(node -> {
                    NodeCollection collection = new NodeCollection();
                    int count = node.node("transactions").getInt(0);
                    for(int i = 0; i < count; i++) {
                        try {
                            ConfigurationNode target = node.node("" + (i + 1));
                            Instant timestamp = target.node("timestamp").get(Instant.class);
                            Currency currency = EconomyService.instance()
                                    .currencies()
                                    .currency(Key.key(target.node("currency").getString()))
                                    .orElse(null);

                            Preconditions.checkNotNull(timestamp);
                            if(since.isBefore(timestamp) && currency != null && account.currency().equals(currency)) {
                                collection.append(target);
                            }
                        }
                        catch (SerializationException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    return collection;
                })
                .flatMap(collection -> collection.valid.stream())
                .map(node -> {
                    try {
                        return new LoggedTransaction(
                                EconomyService.instance()
                                        .currencies()
                                        .currency(Key.key(node.node("currency").getString()))
                                        .orElse(null),
                                BigDecimal.valueOf(node.node("amount").getDouble(0)),
                                EconomyTransactionType.valueOf(node.node("type").getString()),
                                EconomyResultType.valueOf(node.node("result").getString()),
                                node.node("timestamp").get(Instant.class)
                        );
                    }
                    catch (SerializationException e) {
                        throw new RuntimeException(e);
                    }
                })
                .sorted((a, b) -> b.timestamp.compareTo(a.timestamp))
                .forEach(System.out::println);
    }

    @Override
    public boolean purge() throws Exception {
        FileUtils.cleanDirectory(this.root.toFile());
        return true;
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

    private void save(Path target, @NotNull Account account) throws IOException {
        this.createDirectoriesIfNotExists(target.getParent());
        ReentrantLock lock = Objects.requireNonNull(this.ioLocks.get(target));
        lock.lock();

        try {
            ConfigurationNode node;
            if(Files.exists(target)) {
                node = this.loader.loader(target).load();
            } else {
                node = BasicConfigurationNode.root();
            }

            node.node(account.currency().key().asString()).set(account.balance().doubleValue());
            this.loader.loader(target).save(node);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private static final class NodeCollection {
        public final List<ConfigurationNode> valid = Lists.newArrayList();

        public void append(ConfigurationNode node) {
            this.valid.add(node);
        }
    }

    private record LoggedTransaction(
            Currency currency,
            BigDecimal amount,
            EconomyTransactionType type,
            EconomyResultType result,
            Instant timestamp
    ) {}

}
