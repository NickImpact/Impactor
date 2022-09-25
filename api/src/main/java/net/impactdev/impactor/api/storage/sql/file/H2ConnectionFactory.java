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

package net.impactdev.impactor.api.storage.sql.file;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Function;

public class H2ConnectionFactory extends FlatfileConnectionFactory {

    private final Driver driver = null;
    private NonClosableConnection connection;

    public H2ConnectionFactory(Path file) {
        super(file);

//        IsolatedClassLoader classLoader = Impactor.getInstance().getRegistry().get(DependencyManager.class).obtainClassLoaderWith(Lists.newArrayList(ProvidedDependencies.H2));
//        try {
//            Class<?> driverClass = classLoader.loadClass("org.h2.Driver");
//            Method loadMethod = driverClass.getMethod("load");
//            this.driver = (Driver) loadMethod.invoke(null);
//        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Override
    public String getImplementationName() {
        return "H2";
    }

    @Override
    public void shutdown() throws Exception {
        if(this.connection != null) {
            this.connection.shutdown();
        }
    }

    @Override
    public Function<String, String> getStatementProcessor() {
        return s -> s.replace("'", "");
    }

    @Override
    public synchronized Connection getConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            Connection connection = this.driver.connect("jdbc:h2:" + file.toAbsolutePath(), new Properties());
            if (connection != null) {
                this.connection = NonClosableConnection.wrap(connection);
            }
        }

        if (this.connection == null) {
            throw new SQLException("Unable to get a connection.");
        }

        return this.connection;
    }

    @Override
    protected Path getWriteFile() {
        // h2 appends this to the end of the database file
        return super.file.getParent().resolve(super.file.getFileName().toString() + ".mv.db");
    }
}
