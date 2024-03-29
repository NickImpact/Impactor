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

package net.impactdev.impactor.minecraft.items.stacks.providers;

import net.impactdev.impactor.api.items.AbstractedItemStack;
import net.impactdev.impactor.api.items.extensions.BookStack;
import net.impactdev.impactor.minecraft.items.stacks.builders.ImpactorBookStackBuilder;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

public class ImpactorBookStack extends AbstractedItemStack implements BookStack {

    private final BookType bookType;
    private final String author;
    private final Generation generation;
    private final TreeMap<Integer, Component> pages;

    public ImpactorBookStack(ImpactorBookStackBuilder builder) {
        super(builder.type.resolve(), builder);
        this.bookType = builder.type;
        this.author = builder.author;
        this.generation = builder.generation;
        this.pages = builder.pages;
    }

    @Override
    public BookType bookType() {
        return this.bookType;
    }

    @Override
    public String author() {
        return this.author;
    }

    @Override
    public Generation generation() {
        return this.generation;
    }

    @Override
    public int pages() {
        return this.pages.size();
    }

    @Override
    public Component contentAt(int index) {
        return this.pages.get(index);
    }

    @Override
    public List<Component> contents() {
        return new ArrayList<>(this.pages.values());
    }

    @Override
    public CompoundBinaryTag nbt() {
        CompoundBinaryTag nbt = super.nbt();
        nbt = nbt.putString("author", this.author);
        nbt = nbt.putInt("generation", this.generation.ordinal());
        nbt = nbt.putString("title", "Impactor Generated Book");

        int max = this.pages.lastKey();
        ListBinaryTag pages = ListBinaryTag.empty();
        for(int i = 1; i <= max; i++) {
            @Nullable Component result = this.pages.get(i);
            pages = pages.add(StringBinaryTag.of(GsonComponentSerializer.gson().serialize(
                    Optional.ofNullable(result).orElse(Component.empty())
            )));
        }

        return nbt.put("pages", pages);
    }
}
