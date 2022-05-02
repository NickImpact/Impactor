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

package net.impactdev.impactor.common.ui.containers.pagination.sectioned.sections;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.components.TimeoutDetails;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.Section;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.SectionedPage;
import net.impactdev.impactor.common.ui.containers.pagination.sectioned.builders.ImpactorSectionBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractAsynchronousSection extends AbstractSynchronousSection implements Section {

    protected final CompletableFuture<? extends List<? extends Icon<?>>> accumulator;
    protected final Icon<?> waiting;
    protected final TimeoutDetails timeout;

    public AbstractAsynchronousSection(
            ImpactorSectionBuilder<?> builder,
            CompletableFuture<? extends List<? extends Icon<?>>> accumulator,
            Icon<?> waiting,
            TimeoutDetails timeout
    ) {
        super(builder);
        this.accumulator = accumulator;
        this.waiting = waiting;
        this.timeout = timeout;
    }

    protected abstract void queue();

    /**
     * Creates a page which fills the content zone with the given icon.
     *
     * @param icon The icon to fill the zone with.
     */
    protected abstract SectionedPage fill(Icon<?> icon);

    /**
     * If not set, provide an icon that will act as the waiting display to the client
     * within the view.
     *
     * @return The icon to represent the waiting state
     */
    protected abstract Icon<?> waitingIfNotSet();

    /**
     * If not set, provide an icon that will act as the timeout display to the client
     * within the view.
     *
     * @return The icon to represent the timeout state
     */
    protected abstract Icon<?> timeoutIfNotSet();

    protected  <W> CompletableFuture<W> timeoutAfter(long timeout, TimeUnit unit) {
        CompletableFuture<W> result = new CompletableFuture<>();
        Impactor.getInstance().getScheduler().asyncLater(() -> result.completeExceptionally(new TimeoutException()), timeout, unit);
        return result;
    }

    protected  <T> T provide(@Nullable T value, T def) {
        return Optional.ofNullable(value).orElse(def);
    }

}
