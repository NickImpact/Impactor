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

package net.impactdev.impactor.api.storage.connections.sql.file;

import net.impactdev.impactor.api.storage.connections.sql.SqlConnection;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class FlatfileConnection implements SqlConnection {

    private @MonotonicNonNull NonClosableConnection connection;
    private final Path file;

    protected FlatfileConnection(Path file) {
        this.file = file;
    }

    /**
     * Creates a connection to the database
     *
     * @param file A path to the local database file
     * @return A connection to the local database
     * @throws SQLException If any error occurs establishing the connection
     */
    protected abstract Connection createConnection(Path file) throws SQLException;

    @Override
    public synchronized Connection connection() throws SQLException {
        NonClosableConnection connection = this.connection;
        if (connection == null || connection.isClosed()) {
            connection = new NonClosableConnection(createConnection(this.file));
            this.connection = connection;
        }

        return connection;
    }

    @Override
    public void shutdown() throws Exception {
        if(this.connection != null) {
            this.connection.shutdown();
        }
    }

    /**
     * Gets the path of the file the database driver actually ends up writing to.
     *
     * @return the write file
     */
    protected Path getWriteFile() {
        return this.file;
    }
}
