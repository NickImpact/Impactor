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

import com.google.common.collect.Multimap;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.currency.CurrencyProvider;
import net.impactdev.impactor.api.storage.connection.sql.SQLConnection;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.economy.accounts.ImpactorAccount;
import net.impactdev.impactor.core.economy.storage.EconomyStorageImplementation;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.kyori.adventure.key.Key;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public final class SQLProvider implements EconomyStorageImplementation {

    public static final String HAS_ACCOUNT = "SELECT 1 FROM '{prefix}accounts' WHERE uuid = ? AND currency = ?";
    public static final String ACCOUNT = "SELECT * FROM '{prefix}accounts' WHERE uuid = ? AND currency = ?";
    public static final String INSERT_ACCOUNT = "INSERT INTO '{prefix}accounts' (uuid, currency, balance, virtual) VALUES(?, ?, ?, ?)";
    public static final String ALL_ACCOUNTS = "SELECT * FROM '{prefix}accounts'";
    public static final String DELETE_ACCOUNT = "DELETE FROM '{prefix}accounts' WHERE uuid = ? AND currency = ?";
    public static final String TRUNCATE_ACCOUNTS = "TRUNCATE TABLE '{prefix}accounts'";

    private final BaseImpactorPlugin plugin;
    private final SQLConnection factory;
    private final Function<String, String> processor;

    public SQLProvider(SQLConnection connection, String prefix) {
        this.plugin = BaseImpactorPlugin.instance();
        this.factory = connection;
        this.processor = connection.statementProcessor().compose(s -> s.replace("{prefix}", prefix));
    }

    @Override
    public String name() {
        return this.factory.name();
    }

    @Override
    public void init() throws Exception {
        this.factory.init();
        try(InputStream schema = this.plugin.resource(root -> root.resolve("schema").resolve(this.factory.name().toLowerCase() + ".sql"))) {
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(schema, StandardCharsets.UTF_8))) {
                try(Connection connection = this.factory.connection()) {
                    try (Statement s = connection.createStatement()) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("--") || line.startsWith("#")) continue;

                            sb.append(line);

                            // check for end of declaration
                            if (line.endsWith(";")) {
                                sb.deleteCharAt(sb.length() - 1);

                                String result = this.processor.apply(sb.toString().trim());

                                if (!result.isEmpty()) {
                                    if (result.startsWith("set mode")) {
                                        s.addBatch(result);
                                    } else {
                                        if (SchemaReaders.any(this, result)) {
                                            SchemaReaders.first(this, result, s);
                                        }
                                    }
                                }

                                // reset
                                sb = new StringBuilder();
                            }
                        }
                        s.executeBatch();
                    }
                }
            }
        }
    }

    @Override
    public void shutdown() throws Exception {
        this.factory.shutdown();
    }

    @Override
    public void meta(PrettyPrinter printer) throws Exception {
        this.factory.meta(printer);
    }

    @Override
    public boolean hasAccount(Currency currency, UUID uuid) throws Exception {
        return this.query(HAS_ACCOUNT, (connection, ps) -> {
            ps.setBytes(1, this.uuidToBytes(uuid));
            ps.setString(2, currency.key().asString());

            return ps.executeQuery().next();
        });
    }

    @Override
    public Account account(Currency currency, UUID uuid, Account.AccountModifier modifier) throws Exception {
        return this.query(ACCOUNT, (connection, ps) -> {
            ps.setBytes(1, this.uuidToBytes(uuid));
            ps.setString(2, currency.key().asString());

            return this.results(ps, results -> {
                Account account;
                if(results.next()) {
                    Account.AccountBuilder builder = Account.builder()
                            .owner(this.bytesToUUID(results.getBytes("uuid")))
                            .currency(currency)
                            .balance(results.getBigDecimal("balance"));

                    if(results.getBoolean("virtual")) {
                        builder.virtual();
                    }

                    account = builder.build();
                } else {
                    Account.AccountBuilder builder = new ImpactorAccount.ImpactorAccountBuilder();
                    builder.currency(currency).owner(uuid);

                    account = modifier.modify(builder).build();
                    this.save(account);
                }

                return account;
            });
        });
    }

    @Override
    public void save(Account account) throws Exception {
        this.query(INSERT_ACCOUNT, (connection, ps) -> {
            ps.setBytes(1, this.uuidToBytes(account.owner()));
            ps.setString(2, account.currency().key().asString());
            ps.setBigDecimal(3, account.balance());
            ps.setBoolean(4, account.virtual());

            ps.executeUpdate();
            return null;
        });
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public void accounts(Multimap<Currency, Account> cache) throws Exception {
        this.query(ALL_ACCOUNTS, (connection, ps) -> this.results(ps, results -> {
            EconomyService service = EconomyService.instance();
            CurrencyProvider provider = service.currencies();
            while(results.next()) {
                Key key = Key.key(results.getString("currency"));
                Optional<Currency> currency = provider.currency(key);
                if(currency.isPresent()) {
                    Account.AccountBuilder account = Account.builder()
                            .owner(this.bytesToUUID(results.getBytes("uuid")))
                            .currency(currency.get())
                            .balance(results.getBigDecimal("balance"));

                    if(results.getBoolean("virtual")) {
                        account.virtual();
                    }
                    cache.put(currency.get(), account.build());
                }
            }

            return null;
        }));
    }

    @Override
    public void delete(Currency currency, UUID uuid) throws Exception {
        this.query(DELETE_ACCOUNT, (connection, ps) -> {
            ps.setBytes(1, this.uuidToBytes(uuid));
            ps.setString(2, currency.key().asString());

            return null;
        });
    }

    @Override
    public boolean purge() throws Exception {
        return this.query(TRUNCATE_ACCOUNTS, (connection, ps) -> {
            ps.executeUpdate();
            return null;
        });
    }

    private boolean tableExists(String table) throws SQLException {
        try (Connection connection = this.factory.connection()) {
            try (ResultSet rs = connection.getMetaData().getTables(null, null, "%", null)) {
                while (rs.next()) {
                    if (rs.getString(3).equalsIgnoreCase(table)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    private byte[] uuidToBytes(UUID uuid) {
        byte[] bytes = new byte[16];
        ByteBuffer.wrap(bytes)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());

        return bytes;
    }

    private UUID bytesToUUID(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new UUID(buffer.getLong(), buffer.getLong());
    }

    @FunctionalInterface
    private interface Query<T> {
        T prepare(Connection connection, PreparedStatement ps) throws Exception;
    }

    private <T> T query(String key, Query<T> action) throws Exception {
        try(Connection connection = this.factory.connection()) {
            try(PreparedStatement ps = connection.prepareStatement(this.processor.apply(key))) {
                return action.prepare(connection, ps);
            }
        }
    }

    @FunctionalInterface
    private interface Results<T> {
        T results(ResultSet rs) throws Exception;
    }

    private <T> T results(PreparedStatement ps, Results<T> action) throws Exception {
        try(ResultSet rs = ps.executeQuery()) {
            return action.results(rs);
        }
    }

    private enum SchemaReaders {
        CREATE_TABLE((impl, in) -> in.startsWith("CREATE TABLE"), (impl, in) -> !impl.tableExists(getTable(in))),
        ALTER_TABLE((impl, in) -> in.startsWith("ALTER TABLE"), (impl, in) -> impl.tableExists(getTable(in))),
        ANY((impl, input) -> true, (impl, input) -> true);

        private final SchemaPredicate initial;
        private final SchemaPredicate last;

        SchemaReaders(SchemaPredicate initial, SchemaPredicate last) {
            this.initial = initial;
            this.last = last;
        }

        public static boolean any(SQLProvider impl, String in) {
            return Arrays.stream(values()).map(sr -> {
                try {
                    return sr.initial.test(impl, in);
                } catch (Exception e) {
                    ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
                    return false;
                }
            }).filter(x -> x).findAny().orElse(false);
        }

        public static void first(SQLProvider impl, String in, Statement statement) throws Exception {
            for(SchemaReaders reader : SchemaReaders.values()) {
                if(reader != ANY) {
                    if (reader.initial.test(impl, in) && reader.last.test(impl, in)) {
                        statement.addBatch(in);
                        return;
                    }
                } else {
                    for(SchemaReaders r : Arrays.stream(SchemaReaders.values()).filter(sr -> sr != ANY).toList()) {
                        if(r.initial.test(impl, in)) {
                            return;
                        }
                    }

                    statement.addBatch(in);
                }
            }
        }

        private static String getTable(String in) {
            int start = in.indexOf('`');
            return in.substring(start + 1, in.indexOf('`', start + 1));
        }

    }

    private interface SchemaPredicate {

        boolean test(SQLProvider impl, String input) throws Exception;

    }

}
