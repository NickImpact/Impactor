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

import net.impactdev.impactor.api.text.pagination.PaginatedText;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class ActivePagination {

    private static final Component SLASH_TEXT = Component.text("/");
    private static final Component DIVIDER_TEXT = Component.space();
    private static final Component CONTINUATION_TEXT = Component.text("...");
    private static final Page EMPTY_PAGE = new Page(Collections.emptyList());

    private final UUID id = UUID.randomUUID();
    private final @Nullable Component title;
    private final PaginatedText configuration;
    private final Component padding;
    private final int maxContentLinesPerPage;
    private final PaginationCalculator calculator;

    private final Component nextPageText;
    private final Component prevPageText;

    private int page;
    private List<Page> pages;

    ActivePagination(@Nullable Component title, PaginatedText configuration, List<Map.Entry<Component, Integer>> lines, PaginationCalculator calculator) {
        this.title = title;
        this.configuration = configuration;
        this.padding = configuration.padding();
        this.calculator = calculator;

        final AtomicInteger maxContentLinesPerPage = new AtomicInteger(calculator.getMaxLinesPerPage() - 1);
        if (title != null) {
            maxContentLinesPerPage.addAndGet(-calculator.getLines(title));
        }

        this.configuration.header().ifPresent(header -> maxContentLinesPerPage.addAndGet(-calculator.getLines(header)));
        this.configuration.footer().ifPresent(footer -> maxContentLinesPerPage.addAndGet(-calculator.getLines(footer)));

        this.maxContentLinesPerPage = maxContentLinesPerPage.get();
        this.pages = this.constructPages(lines);

        this.nextPageText = Component.text()
                .content("»")
                .color(NamedTextColor.BLUE)
                .decoration(TextDecoration.UNDERLINED, true)
                .clickEvent(ClickEvent.runCommand("/pagination " + this.id + " next"))
                .hoverEvent(HoverEvent.showText(Component.text("/pagination next")))
                .insertion("/pagination " + this.id + ' ' + "next")
                .build();
        this.prevPageText = Component.text()
                .content("«")
                .color(NamedTextColor.BLUE)
                .decoration(TextDecoration.UNDERLINED, true)
                .clickEvent(ClickEvent.runCommand("/pagination " + this.id + " prev"))
                .hoverEvent(HoverEvent.showText(Component.text("/pagination prev")))
                .insertion("/pagination " + this.id + ' ' + "prev")
                .build();
    }

    public UUID id() {
        return this.id;
    }

    public void nextPage(Audience audience) {
        this.send(audience, this.page + 1);
    }

    public void previousPage(Audience audience) {
        this.send(audience, this.page - 1);
    }

    public void send(Audience audience, int page) {
        this.page = page;

        if(this.title != null) {
            audience.sendMessage(this.title);
        }

        this.configuration.header().ifPresent(audience::sendMessage);
        this.page(this.page).send(audience);
        this.configuration.footer().ifPresent(audience::sendMessage);
        Component text = this.calculateFooter(this.page);
        audience.sendMessage(this.calculator.center(text, this.padding));
    }

    private Page page(final int page) {
        final int size = this.pages.size();
        if(size == 0) {
            return EMPTY_PAGE;
        }

        if(page < 1) {
            throw new IllegalArgumentException(String.format("Page %d does not exist!", page));
        } else if(page > size) {
            throw new IllegalArgumentException(String.format("Page %d is greater than the max of %d", page, size));
        }

        return this.pages.get(page - 1);
    }

    private List<Page> constructPages(List<Map.Entry<Component, Integer>> lines) {
        List<Page> results = new ArrayList<>();
        List<Component> currentPage = new ArrayList<>();
        int currentPageLines = 0;

        for(final Map.Entry<Component, Integer> entry : lines) {
            final boolean finiteLinesPerPage = this.getMaxContentLinesPerPage() > 0;
            final boolean willExceedPageLength = entry.getValue() + currentPageLines > this.getMaxContentLinesPerPage();
            final boolean currentPageNotEmpty = currentPageLines != 0;
            final boolean spillToNextPage = finiteLinesPerPage && willExceedPageLength && currentPageNotEmpty;
            if (spillToNextPage) {
                this.padPage(currentPage, currentPageLines, true);
                currentPageLines = 0;
                results.add(new Page(currentPage));
                currentPage = new ArrayList<>();
            }
            currentPageLines += entry.getValue();
            currentPage.add(entry.getKey());
        }

        final boolean lastPageNotEmpty = currentPageLines > 0;
        if (lastPageNotEmpty) {
            if (!results.isEmpty()) {
                // Only pad if we have a previous page
                this.padPage(currentPage, currentPageLines, false);
            }
            results.add(new Page(currentPage));
        }

        return results;
    }

    private void padPage(final List<Component> currentPage, final int currentPageLines, final boolean addContinuation) {
        final int maxContentLinesPerPage = this.getMaxContentLinesPerPage();
        for (int i = currentPageLines; i < maxContentLinesPerPage; i++) {
            if (addContinuation && i == maxContentLinesPerPage - 1) {
                currentPage.add(ActivePagination.CONTINUATION_TEXT);
            } else {
                currentPage.add(0, Component.empty());
            }
        }
    }

    private int getMaxContentLinesPerPage() {
        return this.maxContentLinesPerPage;
    }

    private boolean hasPrevious(final int page) {
        return page > 1;
    }

    private boolean hasNext(final int page) {
        return page < this.pages.size();
    }

    private int getTotalPages() {
        return this.pages.size();
    }

    private Component calculateFooter(final int currentPage) {
        final boolean hasPrevious = this.hasPrevious(currentPage);
        final boolean hasNext = this.hasNext(currentPage);

        final TextComponent.Builder ret = Component.text();
        if (hasPrevious) {
            ret.append(this.prevPageText).append(ActivePagination.DIVIDER_TEXT);
        } else {
            ret.append(Component.text("«")).append(ActivePagination.DIVIDER_TEXT);
        }
        boolean needsDiv = false;
        final int totalPages = this.getTotalPages();
        if (totalPages > 1) {
            ret.append(Component.text()
                    .content(String.valueOf(currentPage))
                    .clickEvent(ClickEvent.runCommand("/pagination " + this.id + ' ' + currentPage))
                    .hoverEvent(HoverEvent.showText(Component.text("/pagination " + currentPage)))
                    .insertion("/pagination " + this.id + ' ' + currentPage)
                    .build());
            ret.append(ActivePagination.SLASH_TEXT);
            ret.append(Component.text()
                    .content(String.valueOf(totalPages))
                    .clickEvent(ClickEvent.runCommand("/pagination " + this.id + ' ' + totalPages))
                    .hoverEvent(HoverEvent.showText(Component.text("/pagination " + totalPages)))
                    .insertion("/pagination " + this.id + ' ' + totalPages)
                    .build());
            needsDiv = true;
        }

        if (needsDiv) {
            ret.append(ActivePagination.DIVIDER_TEXT);
        }

        if (hasNext) {
            ret.append(this.nextPageText);
        } else {
            ret.append(Component.text("»"));
        }

        ret.color(this.padding.color());
        if (this.title != null) {
            ret.style(this.title.style());
        }
        return ret.build();
    }

    private static final class Page {

        private final List<Component> lines;

        public Page(final List<Component> lines) {
            this.lines = lines;
        }

        public void send(Audience audience) {
            for(Component line : this.lines) {
                audience.sendMessage(line);
            }
        }
    }
}
