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

package net.impactdev.impactor.api.storage.connection.sql;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.storage.StorageCredentials;
import net.impactdev.impactor.api.storage.connection.sql.file.H2Connection;
import net.impactdev.impactor.api.storage.connection.sql.hikari.MariaDBConnection;
import net.impactdev.impactor.api.storage.connection.sql.hikari.MySQLConnection;

import java.nio.file.Path;

public class SQLConnections {

    public static H2Connection h2(Path target) {
        return Impactor.getInstance().getRegistry()
                .createBuilder(H2Connection.H2ConnectionBuilder.class)
                .file(target)
                .build();
    }

    public static MySQLConnection mysql(StorageCredentials credentials) {
        return Impactor.getInstance().getRegistry()
                .createBuilder(MySQLConnection.MySQLConnectionBuilder.class)
                .credentials(credentials)
                .build();
    }

    public static MariaDBConnection mariaDB(StorageCredentials credentials) {
        return Impactor.getInstance().getRegistry()
                .createBuilder(MariaDBConnection.MariaDBConnectionBuilder.class)
                .credentials(credentials)
                .build();
    }

}
