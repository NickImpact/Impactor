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

package net.impactdev.impactor.sponge.ui.containers.sectioned.builders;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.SectionedPagination;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.builders.SectionBuilder;
import net.impactdev.impactor.common.ui.pagination.sectioned.builders.ImpactorSectionedPaginationBuilder;
import net.impactdev.impactor.sponge.ui.containers.sectioned.SpongeSectionedPagination;

public class SpongeSectionedPaginationBuilder extends ImpactorSectionedPaginationBuilder {

    @Override
    public SectionSelector section() {
        return new SpongeSectionSelector(this);
    }

    @Override
    public SectionedPagination build() {
        return new SpongeSectionedPagination(this);
    }

    public static class SpongeSectionSelector implements SectionSelector {

        private final SpongeSectionedPaginationBuilder parent;

        private SpongeSectionSelector(SpongeSectionedPaginationBuilder parent) {
            this.parent = parent;
        }

        @Override
        public SectionBuilder.Synchronous.Basic synchronous() {
            return new SpongeSynchronousBasicSectionBuilder(this.parent);
        }

        @Override
        public <T> SectionBuilder.Synchronous.Generic<T> synchronous(TypeToken<T> type) {
            return new SpongeSynchronousGenericSectionBuilder<>(this.parent);
        }

        @Override
        public SectionBuilder.Asynchronous.Basic asynchronous() {
            return new SpongeAsynchronousBasicSectionBuilder(this.parent);
        }

        @Override
        public <T> SectionBuilder.Asynchronous.Generic<T> asynchronous(TypeToken<T> type) {
            return new SpongeAsynchronousGenericSectionBuilder<>(this.parent);
        }
    }
}
