package net.impactdev.impactor.api.economy.builtin.storage.implementations;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.impactdev.impactor.api.core.logging.ExceptionPrinter;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.builtin.accounts.ImpactorAccount;
import net.impactdev.impactor.api.economy.builtin.storage.EconomyStorageImplementation;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.storage.StorageMetadata;
import net.impactdev.impactor.api.storage.connections.flatfile.ConfigurateLoader;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurateProvider implements EconomyStorageImplementation {

    private final Path root = Paths.get("impactor").resolve("economy");
    private final Path accounts = this.root.resolve("accounts");
    private final Path performance = this.root.resolve("performance.conf");

    private final ConfigurateLoader loader;
    private final LoadingCache<Path, ReentrantLock> locks;

    public ConfigurateProvider(ConfigurateLoader loader) {
        this.loader = loader;
        this.locks = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(key -> new ReentrantLock());
    }

    @Override
    public String name() {
        return this.loader.name();
    }

    @Override
    public void init() throws Exception {
        this.createDirectoriesIfNotExists(this.accounts);
    }

    @Override
    public void shutdown() throws Exception {}

    @Override
    public StorageMetadata metadata() throws Exception {
        StorageMetadata metadata = new StorageMetadata();
        Function<Path, Long> sizing = path -> {
            try {
                return Files.size(path);
            } catch (IOException e) {
                return 0L;
            }
        };

        AtomicLong sum = new AtomicLong(sizing.apply(this.performance));
        try (Stream<Path> accounts = Files.walk(this.accounts)) {
            accounts.filter(Files::isRegularFile).forEach(path -> sum.addAndGet(sizing.apply(path)));
        }

        return metadata.with(StorageMetadata.DATA_SIZE, sum.get());
    }

    @Override
    public boolean exists(UUID uuid, Currency currency) throws Exception {
        Path target = this.setupUUIDTree(this.accounts, uuid).resolve("balances.conf");
        if (Files.exists(target)) {
            ConfigurationNode data = this.read(target);
            return !data.node(currency.key().toString()).virtual();
        }

        return false;
    }

    @Override
    public Account account(UUID uuid, Currency currency) throws Exception {
        Path target = this.setupUUIDTree(this.accounts, uuid).resolve("balances.conf");

        ReentrantLock lock = this.locks.get(target);
        lock.lock();

        try {
            if (Files.exists(target)) {
                ConfigurationNode data = this.read(target).node(currency.key().toString());

                if (!data.virtual()) {
                    return ImpactorAccount.load(uuid, currency, BigDecimal.valueOf(data.getDouble()));
                }
            }

            Account created = ImpactorAccount.create(uuid, currency);
            this.save(created);

            return created;
        } catch (Exception e) {
            ExceptionPrinter.print(e);
            throw e;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void save(Account account) throws Exception {
        Path target = this.setupUUIDTree(this.accounts, account.owner()).resolve("balances.conf");
        this.createDirectoriesIfNotExists(target.getParent());

        ReentrantLock lock = this.locks.get(target);
        lock.lock();

        try {
            ConfigurationNode node = Files.exists(target) ? this.loader.loader(target).load() : BasicConfigurationNode.root();

            node.node(account.currency().key().toString()).set(account.balance().doubleValue());
            this.loader.loader(target).save(node);
        } catch (Exception e) {
            ExceptionPrinter.print(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(UUID uuid, Currency currency) throws Exception {
        Path target = this.setupUUIDTree(this.accounts, uuid).resolve("balances.conf");
        if (!Files.exists(target)) {
            return;
        }

        ReentrantLock lock = this.locks.get(target);
        lock.lock();

        try {
            ConfigurationNode node = this.loader.loader(target).load();
            node.removeChild(currency.key().toString());

            this.loader.loader(target).save(node);
        } catch (Exception e) {
            ExceptionPrinter.print(e);
            throw e;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<Account> performing(Currency currency, int limit) throws Exception {
        Path target = this.performance;

        if (!Files.exists(target)) {
            return Collections.emptySet();
        }

        ReentrantLock lock = this.locks.get(target);
        lock.lock();

        try {
            ConfigurationNode node = this.loader.loader(target).load().node(currency.key().toString());
            List<UUID> accounts = Objects.requireNonNull(node.getList(String.class))
                    .stream()
                    .map(UUID::fromString)
                    .limit(limit)
                    .toList();

            return accounts.stream()
                    .map(uuid -> {
                        try {
                            return this.account(uuid, currency);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            ExceptionPrinter.print(e);
            throw e;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void log(EconomyTransaction transaction) throws Exception {
        Path target = this.setupUUIDTree(this.accounts, transaction.account().owner()).resolve("logs");

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());
        Path branch = target.resolve(date.format(transaction.timestamp()));
        Path leaf = branch.resolve("transactions.log");

        this.createDirectoriesIfNotExists(branch);

        ReentrantLock lock = this.locks.get(leaf);
        lock.lock();

        try {
            ConfigurationNode node = Files.exists(leaf) ? this.loader.loader(leaf).load() : BasicConfigurationNode.root();
            ConfigurationNode transactions = node.node("transactions");

            ConfigurationNode details = transactions.appendListNode();
            details.node("currency").set(transaction.currency().key().toString());
            details.node("type").set(transaction.type().toString());
            details.node("amount").set(transaction.amount().doubleValue());
            details.node("result").set(transaction.result().toString());
            details.node("timestamp").set(date.format(transaction.timestamp()));

            this.loader.loader(target).save(node);
        } catch (Exception e) {
            ExceptionPrinter.print(e);
        } finally {
            lock.unlock();
        }
    }

    private void createDirectoriesIfNotExists(Path path) throws IOException {
        if (!Files.exists(path) || (!Files.isDirectory(path) && !Files.isSymbolicLink(path))) {
            Files.createDirectories(path);
        }
    }

    private Path setupUUIDTree(Path root, UUID target) {
        return root.resolve(target.toString().substring(0, 2)).resolve(target.toString());
    }

    private ConfigurationNode read(Path path) throws IOException {
        ReentrantLock lock = this.locks.get(path);
        lock.lock();

        try {
            if (!Files.exists(path)) {
                throw new FileNotFoundException("Target file does not exist: " + path);
            }

            return this.loader.loader(path).load();
        } finally {
            lock.unlock();
        }
    }
}
