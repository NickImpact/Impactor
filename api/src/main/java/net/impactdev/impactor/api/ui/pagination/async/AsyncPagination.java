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

package net.impactdev.impactor.api.ui.pagination.async;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.components.UIComponent;
import net.impactdev.impactor.api.ui.icons.Icon;
import net.impactdev.impactor.api.ui.pagination.Pagination;
import net.impactdev.impactor.api.ui.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.utilities.Builder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.TriState;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncPagination extends Pagination {

    static AsyncPaginationBuilder builder() {
        return Impactor.getInstance().getRegistry().createBuilder(AsyncPaginationBuilder.class);
    }

    interface AsyncPaginationBuilder extends UIComponent<AsyncPaginationBuilder>, Builder<AsyncPagination, AsyncPaginationBuilder> {

        @Required
        AsyncPaginationBuilder provider(Key key);

        @Required
        AsyncPaginationBuilder viewer(PlatformPlayer viewer);

        @Required
        AsyncPaginationBuilder accumulator(CompletableFuture<List<Icon<?>>> provider);

        AsyncPaginationBuilder zone(Vector2i dimensions);

        AsyncPaginationBuilder zone(Vector2i dimensions, @Nullable Vector2i offset);

        AsyncPaginationBuilder updater(PageUpdater updater);

        AsyncPaginationBuilder style(TriState state);

        AsyncPaginationBuilder onTimeout(Icon<?> icon);

        AsyncPaginationBuilder waiting(Icon<?> icon);

    }

}
