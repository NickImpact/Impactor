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

package net.impactdev.impactor.core.utility.pointers;

import net.impactdev.impactor.api.utility.pointers.PointerCapable;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.pointer.Pointers;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class AbstractPointerCapable implements PointerCapable {

    private Pointers pointers = Pointers.empty();

    @Override
    public @NotNull Pointers pointers() {
        return this.pointers;
    }

    @Override
    public <T> PointerCapable with(Pointer<T> pointer, T value) {
        this.pointers = this.pointers.toBuilder().withStatic(pointer, value).build();
        return this;
    }

    @Override
    public <T> PointerCapable withDynamic(Pointer<T> pointer, Supplier<T> supplier) {
        this.pointers = this.pointers.toBuilder().withDynamic(pointer, supplier).build();
        return this;
    }
}
