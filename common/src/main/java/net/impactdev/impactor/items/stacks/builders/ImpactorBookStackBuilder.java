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

package net.impactdev.impactor.items.stacks.builders;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.builders.AbstractStackBuilder;
import net.impactdev.impactor.api.items.builders.provided.BookStackBuilder;
import net.impactdev.impactor.api.items.extensions.BookStack;
import net.impactdev.impactor.items.stacks.providers.ImpactorBookStack;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Range;

import java.util.Collection;
import java.util.TreeMap;

public class ImpactorBookStackBuilder
        extends AbstractStackBuilder<BookStack, BookStackBuilder>
        implements BookStackBuilder
{

    public BookStack.BookType type;
    public String author;
    public BookStack.Generation generation;
    public TreeMap<Integer, Component> pages = Maps.newTreeMap();

    @Override
    public BookStackBuilder type(BookStack.BookType type) {
        this.type = type;
        return this;
    }

    @Override
    public BookStackBuilder title(Component title) {
        return super.title(title);
    }

    @Override
    public BookStackBuilder author(String author) {
        this.author = author;
        return this;
    }

    @Override
    public BookStackBuilder generation(BookStack.Generation generation) {
        this.generation = generation;
        return this;
    }

    @Override
    public BookStackBuilder page(@Range(from = 1, to = 50) int page, Component content) {
        this.pages.put(page, content);
        return this;
    }

    @Override
    public BookStackBuilder pages(Component... contents) {
        int page = 1;
        for(Component content : contents) {
            this.pages.put(page++, content);
        }
        return this;
    }

    @Override
    public BookStackBuilder pages(Collection<Component> contents) {
        int page = 1;
        for(Component content : contents) {
            this.pages.put(page++, content);
        }
        return this;
    }

    @Override
    public BookStack build() {
        return new ImpactorBookStack(this);
    }
}
