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

package net.impactdev.impactor.sponge.ui.containers.pagination.builders;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.ui.containers.pagination.Pagination;
import net.impactdev.impactor.common.ui.containers.pagination.builders.ImpactorPaginationBuilder;
import net.impactdev.impactor.sponge.ui.containers.pagination.implementations.asynchronous.SpongeAsynchronousBasicPagination;
import net.impactdev.impactor.sponge.ui.containers.pagination.implementations.asynchronous.SpongeAsynchronousGenericPagination;
import net.impactdev.impactor.sponge.ui.containers.pagination.implementations.synchronous.SpongeSynchronousGenericPagination;
import net.impactdev.impactor.sponge.ui.containers.pagination.implementations.synchronous.SpongeSynchronousBasicPagination;

public class SpongeImpactorPaginationBuilder extends ImpactorPaginationBuilder {

    @Override
    public Synchronous.Basic synchronous() {
        return new SpongeSynchronousPaginationBuilder(this);
    }

    @Override
    public <T> Synchronous.Generic<T> synchronous(TypeToken<T> type) {
        return new SpongeSynchronousGenericPaginationBuilder<>(this);
    }

    @Override
    public Asynchronous.Basic asynchronous() {
        return new SpongeAsynchronousPaginationBuilder(this);
    }

    @Override
    public <T> Asynchronous.Generic<T> asynchronous(TypeToken<T> type) {
        return new SpongeAsynchronousGenericPaginationBuilder<>(this);
    }

    public static class SpongeSynchronousPaginationBuilder extends ImpactorSynchronousBuilder {

        public SpongeSynchronousPaginationBuilder(ImpactorPaginationBuilder parent) {
            super(parent);
        }

        @Override
        public Pagination complete() {
            return new SpongeSynchronousBasicPagination(this);
        }
    }

    public static class SpongeSynchronousGenericPaginationBuilder<T> extends ImpactorSynchronousGenericBuilder<T> {

        public SpongeSynchronousGenericPaginationBuilder(ImpactorPaginationBuilder parent) {
            super(parent);
        }

        @Override
        public Pagination.Generic<T> complete() {
            return new SpongeSynchronousGenericPagination<>(this);
        }
    }

    public static class SpongeAsynchronousPaginationBuilder extends ImpactorAsynchronousBuilder {

        protected SpongeAsynchronousPaginationBuilder(ImpactorPaginationBuilder parent) {
            super(parent);
        }

        @Override
        public Pagination complete() {
            return new SpongeAsynchronousBasicPagination(this);
        }

    }

    public static class SpongeAsynchronousGenericPaginationBuilder<T> extends ImpactorAsynchronousGenericBuilder<T> {

        public SpongeAsynchronousGenericPaginationBuilder(ImpactorPaginationBuilder parent) {
            super(parent);
        }

        @Override
        public Pagination.Generic<T> complete() {
            return new SpongeAsynchronousGenericPagination<>(this);
        }
    }
}
