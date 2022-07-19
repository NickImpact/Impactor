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

package net.impactdev.impactor.common.storage.sql.file;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.dependencies.DependencyManager;
import net.impactdev.impactor.api.dependencies.ProvidedDependencies;
import net.impactdev.impactor.api.dependencies.classloader.IsolatedClassLoader;
import net.impactdev.impactor.api.storage.connection.sql.file.H2Connection;
import net.impactdev.impactor.api.storage.connection.sql.file.NonClosableConnection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Function;

public class H2ConnectionImpl extends FlatfileConnection implements H2Connection {

    private final Driver driver;
    private NonClosableConnection connection;

    public H2ConnectionImpl(H2ConnectionBuilderImpl builder) {
        super(builder.target);

        IsolatedClassLoader classLoader = Impactor.getInstance().getRegistry().get(DependencyManager.class).obtainClassLoaderWith(
                Lists.newArrayList(ProvidedDependencies.H2));
        try {
            Class<?> driverClass = classLoader.loadClass("org.h2.Driver");
            Method loadMethod = driverClass.getMethod("load");
            this.driver = (Driver) loadMethod.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String name() {
        return "H2";
    }

    @Override
    public void shutdown() throws Exception {
        if(this.connection != null) {
            this.connection.shutdown();
        }
    }

    @Override
    public synchronized Connection connection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            Connection connection = this.driver.connect("jdbc:h2:" + this.target().toAbsolutePath(), new Properties());
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
    public Function<String, String> getStatementProcessor() {
        return s -> s.replace("'", "");
    }

    public static class H2ConnectionBuilderImpl implements H2ConnectionBuilder {

        private Path target;

        @Override
        public H2ConnectionBuilder file(Path target) {
            this.target = target;
            return this;
        }

        @Override
        public H2Connection build() {
            return new H2ConnectionImpl(this);
        }

    }
}
