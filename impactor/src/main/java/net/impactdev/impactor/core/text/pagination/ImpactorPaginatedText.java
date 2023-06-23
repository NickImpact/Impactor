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

package net.impactdev.impactor.core.text.pagination;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.text.pagination.PaginatedText;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkNotNull;

public class ImpactorPaginatedText implements PaginatedText {

    static final Component DEFAULT_PADDING = Component.text("=");

    private final Component title;
    private final Component header;
    private final Component footer;
    private final Component padding;
    private final List<Component> contents;
    private final int lines;

    public ImpactorPaginatedText(ImpactorPaginationBuilder builder) {
        this.title = builder.title;
        this.header = builder.header;
        this.footer = builder.footer;
        this.padding = builder.padding;
        this.contents = builder.contents;
        this.lines = builder.lines;
    }

    @Override
    public Optional<Component> title() {
        return Optional.ofNullable(this.title);
    }

    @Override
    public List<Component> contents() {
        return this.contents;
    }

    @Override
    public Optional<Component> header() {
        return Optional.ofNullable(this.header);
    }

    @Override
    public Optional<Component> footer() {
        return Optional.ofNullable(this.footer);
    }

    @Override
    public Component padding() {
        return this.padding;
    }

    @Override
    public int lines() {
        return this.lines;
    }

    @Override
    public void send(@NotNull Audience audience, int page) {
        checkNotNull(audience, "The audience of a pagination cannot be null");

        final PaginationCalculator calculator = new PaginationCalculator(this.lines);
        final List<Map.Entry<Component, Integer>> counts = StreamSupport.stream(this.contents().spliterator(), false)
                .map(input -> {
                    final int lines = calculator.getLines(input);
                    return Maps.immutableEntry(input, lines);
                }).collect(Collectors.toList());

        Component title = this.title;
        if(title != null) {
            title = calculator.center(title, this.padding);
        } else {
            title = calculator.center(Component.empty(), this.padding);
        }

        final ActivePagination active = new ActivePagination(title, this, counts, calculator);
        final PaginationService service = Impactor.instance().services().provide(PaginationService.class);
        service.register(active.id(), active);

        try {
            active.send(audience, 1);
        } catch (Exception e) {
            ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
        }
    }

    public static class ImpactorPaginationBuilder implements PaginatedTextBuilder {

        private Component title;
        private Component header;
        private Component footer;
        private Component padding;
        private List<Component> contents = new ArrayList<>();
        private int lines;

        @Override
        public PaginatedTextBuilder title(Component title) {
            this.title = title;
            return this;
        }

        @Override
        public PaginatedTextBuilder contents(Component... contents) {
            this.contents.addAll(Arrays.asList(contents));
            return this;
        }

        @Override
        public PaginatedTextBuilder contents(Collection<Component> contents) {
            this.contents.addAll(contents);
            return this;
        }

        @Override
        public PaginatedTextBuilder header(Component header) {
            this.header = header;
            return this;
        }

        @Override
        public PaginatedTextBuilder footer(Component footer) {
            this.footer = footer;
            return this;
        }

        @Override
        public PaginatedTextBuilder padding(Component padding) {
            this.padding = padding;
            return this;
        }

        @Override
        public PaginatedTextBuilder lines(int lines) {
            this.lines = lines;
            return this;
        }

        @Override
        public PaginatedText build() {
            return new ImpactorPaginatedText(this);
        }
    }
}
