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

package net.impactdev.impactor.api.storage;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.pointer.Pointers;
import org.jetbrains.annotations.NotNull;

public final class StorageMetadata implements Pointered {

    public static Pointer<Integer> PING = Pointer.pointer(Integer.class, Key.key("impactor", "ping"));
    public static Pointer<Long> FILE_SIZE = Pointer.pointer(Long.class, Key.key("impactor", "file_size"));
    public static Pointer<Long> DATA_SIZE = Pointer.pointer(Long.class, Key.key("impactor", "data_size"));

    private final Pointers pointers;

    public StorageMetadata() {
        this.pointers = Pointers.empty();
    }

    private StorageMetadata(final Pointers pointers) {
        this.pointers = pointers;
    }

    public <T> StorageMetadata with(Pointer<T> pointer, T value) {
        return new StorageMetadata(this.pointers.toBuilder().withStatic(pointer, value).build());
    }

    @Override
    public @NotNull Pointers pointers() {
        return this.pointers;
    }
}
