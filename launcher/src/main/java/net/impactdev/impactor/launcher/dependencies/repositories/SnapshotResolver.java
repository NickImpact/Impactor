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

package net.impactdev.impactor.launcher.dependencies.repositories;

import com.google.common.base.Preconditions;
import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.builders.Required;
import net.impactdev.impactor.launcher.dependencies.Dependency;

import java.net.URL;

public final class SnapshotResolver {

    private final URL path;
    private final Resolver resolver;

    private SnapshotResolver(SnapshotResolverBuilder builder) {
        this.path = builder.path;
        this.resolver = builder.resolver;
    }

    public URL path() {
        return this.path;
    }

    public URL resolve(Dependency dependency) {
        return this.resolver.create(this.path, dependency);
    }

    @FunctionalInterface
    interface Resolver {

        URL create(URL parent, Dependency dependency);

    }

    static SnapshotResolverBuilder builder() {
        return new SnapshotResolverBuilder();
    }

    public static class SnapshotResolverBuilder implements Builder<SnapshotResolver> {

        private URL path;
        private Resolver resolver;

        @Required
        public SnapshotResolverBuilder path(URL path) {
            this.path = path;
            return this;
        }

        @Required
        public SnapshotResolverBuilder resolver(Resolver resolver) {
            this.resolver = resolver;
            return this;
        }

        @Override
        public SnapshotResolver build() {
            Preconditions.checkNotNull(this.path, "path");
            Preconditions.checkNotNull(this.resolver, "resolver");
            return new SnapshotResolver(this);
        }
    }
}
