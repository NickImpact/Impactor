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

package net.impactdev.impactor.api.ui.containers.pagination.sectioned.builders;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.builders.Required;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.components.UIComponent;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.SectionedPagination;
import net.kyori.adventure.key.Key;

public interface SectionedPaginationBuilder extends UIComponent<SectionedPaginationBuilder>, Builder<SectionedPagination> {

    @Required
    SectionedPaginationBuilder provider(Key provider);

    @Required
    SectionedPaginationBuilder viewer(PlatformPlayer viewer);

    SectionSelector section();

    interface SectionSelector {

        SectionBuilder.Synchronous.Basic synchronous();

        default <T> SectionBuilder.Synchronous.Generic<T> synchronous(Class<T> type) {
            return this.synchronous(TypeToken.get(type));
        }

        <T> SectionBuilder.Synchronous.Generic<T> synchronous(TypeToken<T> type);

        SectionBuilder.Asynchronous.Basic asynchronous();

        default <T> SectionBuilder.Asynchronous.Generic<T> asynchronous(Class<T> type) {
            return this.asynchronous(TypeToken.get(type));
        }

        <T> SectionBuilder.Asynchronous.Generic<T> asynchronous(TypeToken<T> type);

    }

}
