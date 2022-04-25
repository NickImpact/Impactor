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

package net.impactdev.impactor.api.ui.containers.pagination.builders;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.ui.containers.pagination.Pagination;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.SectionedPagination;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.spongepowered.math.vector.Vector2i;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class Tester {

    public void pagination() {
        Pagination.builder()
                .provider(Key.key("test", "test"))
                .title(Component.empty())
                .readonly(true)
                .zone(Vector2i.from(7, 3))
                .asynchronous()
                .accumulator(CompletableFuture.completedFuture(Lists.newArrayList()))
                .build();

        Pagination test2 = Pagination.builder()
                .provider(Key.key("test", "test2"))
                .title(Component.empty())
                .zone(Vector2i.from(7, 3))
                .synchronous()
                .contents(Lists.newArrayList())
                .build();

        Pagination.Generic<Integer> test = Pagination.builder()
                .synchronous(Integer.class)
                .contents(Lists.newArrayList())
                .sort(Comparator.naturalOrder())
                .build();
    }

    public void sections() {
        SectionedPagination.builder()
                .provider(null)
                .viewer(null)
                .section()
                .synchronous()
                .contents(Lists.newArrayList())
                .dimensions(Vector2i.ONE)
                .complete()
                .build();

        SectionedPagination.builder()
                .provider(null)
                .viewer(null)
                .section()
                .asynchronous()
                .accumulator(CompletableFuture.completedFuture(Lists.newArrayList()))
                .complete()
                .build();
    }

}
