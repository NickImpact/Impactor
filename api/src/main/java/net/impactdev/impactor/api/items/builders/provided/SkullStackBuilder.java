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

import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.items.builders.ImpactorItemStackBuilder;
import net.impactdev.impactor.api.items.extensions.SkullStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface SkullStackBuilder extends ImpactorItemStackBuilder<SkullStackBuilder> {

    @Contract("-> !null")
    Mob mob();

    @Contract("-> !null")
    Player player();

    interface Mob {

        @Contract("_ -> !null")
        SkullStackBuilder type(final SkullStack.SkullType type);

    }

    interface Player extends Builder.Child<SkullStackBuilder> {

        @Contract("_ -> this")
        Player of(final String target);

        @Contract("_ -> this")
        Player texture(@NotNull final String texture);

    }

}
