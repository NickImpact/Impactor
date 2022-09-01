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

package net.impactdev.impactor.api.items.extensions;

import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.types.ItemType;
import net.impactdev.impactor.api.items.types.ItemTypes;
import net.kyori.adventure.text.Component;

import java.util.List;

public interface BookStack extends ImpactorItemStack {

    BookType bookType();

    @Override
    Component title();

    Component author();

    Generation generation();

    int pages();

    Component contentAt(int index);

    List<Component> contents();

    enum BookType {

        WRITABLE(ItemTypes.WRITABLE_BOOK),
        WRITTEN(ItemTypes.WRITTEN_BOOK);

        private final ItemType type;

        BookType(ItemType type) {
            this.type = type;
        }

        public ItemType resolve() {
            return this.type;
        }
    }

    enum Generation {

        ORIGINAL,
        COPY,
        COPY_OF_COPY,
        TATTERED

    }

}
