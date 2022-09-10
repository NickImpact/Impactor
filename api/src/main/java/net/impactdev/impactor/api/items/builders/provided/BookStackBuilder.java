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

package net.impactdev.impactor.api.items.builders.provided;

import net.impactdev.impactor.api.items.builders.ImpactorItemStackBuilder;
import net.impactdev.impactor.api.items.extensions.BookStack;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;

import java.util.Collection;

public interface BookStackBuilder extends ImpactorItemStackBuilder<BookStack, BookStackBuilder> {

    @Contract
    BookStackBuilder type(BookStack.BookType type);

    /**
     * Sets the title of the book. Note that a title for a book in Minecraft is limited
     * to only 32 characters!
     *
     * @param title The title for the book
     * @return This builder
     */
    @Override
    @Contract("_ -> this")
    BookStackBuilder title(final Component title);

    /**
     * Sets the author of this book.
     *
     * @param author A component describing the name of the author
     * @return This builder
     */
    @Contract("_ -> this")
    BookStackBuilder author(final String author);

    /**
     * Sets the generation of this book. In minecraft, the generation of a book indicates its source
     * of creation. There are 4 different states, with only 3 actually in use. These states are
     * {@link BookStack.Generation#ORIGINAL original}, {@link BookStack.Generation#COPY copy},
     * {@link BookStack.Generation#COPY_OF_COPY copy of a copy}, and {@link BookStack.Generation#TATTERED tattered}.
     * Tattered is the only unused generation in Minecraft, and functions 1:1 as a copy of a copy.
     *
     * @param generation The target generation of a book
     * @return This builder
     */
    @Contract("_ -> this")
    BookStackBuilder generation(final BookStack.Generation generation);

    /**
     * Sets a singular page's written contents to the following component.
     *
     * <p>NOTE: A page can only accept 256 characters, so be sure to limit a component's
     * text to this maximum.
     *
     * @param page A target page, from 1 to 50 inclusively
     * @param content A component representing the content to be written to the page.
     * @return This builder
     */
    @Contract("_,_ -> this")
    BookStackBuilder page(@Range(from = 1, to = 50) final int page, Component content);

    /**
     * Sets this book's pages to the following components, in the order they appear in the given
     * array. So, the first component would be page 1, with each additional component representing
     * the next page.
     *
     * <p>NOTE: Due to game limitations, this will only accept the first 50 options, ignoring
     * any additional components beyond this limit. Additionally, each component is limited to 256
     * characters.
     *
     * @param contents A variable-sized array of components representing a collection of pages
     * @return This builder
     */
    @Contract("_ -> this")
    BookStackBuilder pages(final Component... contents);

    /**
     * Sets this book's pages to the following components, in the order they appear in the given
     * collection. So, the first component would be page 1, with each additional component representing
     * the next page.
     *
     * <p>NOTE: Due to game limitations, this will only accept the first 50 options, ignoring
     * any additional components beyond this limit. Additionally, each component is limited to 256
     * characters.
     *
     * @param contents A collection of components representing a set of pages
     * @return This builder
     */
    @Contract("_ -> this")
    BookStackBuilder pages(final Collection<Component> contents);

}
