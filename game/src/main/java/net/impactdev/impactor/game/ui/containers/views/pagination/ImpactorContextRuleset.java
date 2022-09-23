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

package net.impactdev.impactor.game.ui.containers.views.pagination;

import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.views.pagination.rules.ContextRuleset;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ImpactorContextRuleset implements ContextRuleset {

    private PaginatedView parent;
    private Predicate<Icon> filter = null;
    private Comparator<Icon> sorter = null;

    public ImpactorContextRuleset with(PaginatedView parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public void filter(Predicate<Icon> filter) {
        this.filter = filter;
        if(this.parent != null) {
            this.parent.update();
        }
    }

    @Override
    public void sorter(Comparator<Icon> sorter) {
        this.sorter = sorter;
        if(this.parent != null) {
            this.parent.update();
        }
    }

    @Override
    public Stream<Icon> apply(Stream<Icon> stream) {
        stream = Optional.ofNullable(this.filter).map(stream::filter).orElse(stream);
        return Optional.ofNullable(this.sorter).map(stream::sorted).orElse(stream);
    }

    public static class ContextRulesetFactory implements Factory {

        @Override
        public ContextRuleset create() {
            return new ImpactorContextRuleset();
        }

    }

}
