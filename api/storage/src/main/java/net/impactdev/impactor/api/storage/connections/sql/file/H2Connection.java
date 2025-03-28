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

import net.impactdev.impactor.api.storage.StorageMetadata;
import org.h2.jdbc.JdbcConnection;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class H2Connection extends FlatfileConnection {

    public H2Connection(Path file) {
        super(file);
    }

    @Override
    public String name() {
        return "H2";
    }

    @Override
    public void init() throws Exception {}

    @Override
    public StorageMetadata metadata() throws Exception {
        return null;
    }

    @Override
    protected Connection createConnection(Path file) throws SQLException {
        return new JdbcConnection(
                "jdbc:h2:" + file.toString(),
                new Properties(),
                null,
                null,
                false
        );
    }

    @Override
    protected Path getWriteFile() {
        // h2 appends '.mv.db' to the end of the database name
        Path writeFile = super.getWriteFile();
        return writeFile.getParent().resolve(writeFile.getFileName().toString() + ".mv.db");
    }
}
