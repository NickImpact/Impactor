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

package net.impactdev.impactor.api.storage;

import java.util.Set;

public final class StorageTypes {

    // Configurate
    public static final StorageType YAML = new StorageTyping("YAML", Set.of("yaml", "yml"));
    public static final StorageType JSON = new StorageTyping("JSON", Set.of("json", "flatfile"));
    public static final StorageType HOCON = new StorageTyping("HOCON", Set.of("hocon"));

    // Remote Databases
    public static final StorageType MYSQL = new StorageTyping("MySQL", Set.of("mysql"));
    public static final StorageType MARIADB = new StorageTyping("MariaDB", Set.of("mariadb"));
    public static final StorageType MONGODB = new StorageTyping("MongoDB", Set.of("mongodb"));
    public static final StorageType POSTGRESQL = new StorageTyping("PostgreSQL", Set.of("postgresql"));

    // Local Databases
    public static final StorageType SQLITE = new StorageTyping("Sqlite", Set.of("sqlite"));
    public static final StorageType H2 = new StorageTyping("H2", Set.of("h2"));

    record StorageTyping(String name, Set<String> identifiers) implements StorageType {}
}
