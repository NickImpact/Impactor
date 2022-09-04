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

package net.impactdev.impactor.api.ui.containers.views.pagination.updaters;

import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.items.ImpactorItemStack;

public final class PageUpdater {

    private final PageUpdaterType type;
    private final int slot;
    private final UpdaterProvider provider;

    private PageUpdater(PageUpdaterType type, int slot, UpdaterProvider provider) {
        this.type = type;
        this.slot = slot;
        this.provider = provider;
    }

    public PageUpdaterType type() {
        return this.type;
    }

    public int slot() {
        return this.slot;
    }

    public UpdaterProvider provider() {
        return this.provider;
    }

    public static PageUpdaterBuilder builder() {
        return new PageUpdaterBuilder();
    }

    public static class PageUpdaterBuilder implements Builder<PageUpdater> {

        private PageUpdaterType type;
        private int slot;
        private UpdaterProvider provider;

        public PageUpdaterBuilder type(PageUpdaterType type) {
            this.type = type;
            return this;
        }

        public PageUpdaterBuilder slot(int slot) {
            this.slot = slot;
            return this;
        }

        public PageUpdaterBuilder provider(UpdaterProvider provider) {
            this.provider = provider;
            return this;
        }

        @Override
        public PageUpdater build() {
            return new PageUpdater(this.type, this.slot, this.provider);
        }
    }

    @FunctionalInterface
    public interface UpdaterProvider {

        /**
         * Provides a platform based ItemStack that will act as the client-facing
         * display of the icon representing
         *
         * @param target The intended target page of the updater if clicked
         * @return A displayable item that will be placed within an Icon
         */
        ImpactorItemStack provide(int target);

    }
}
