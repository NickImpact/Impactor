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

package net.impactdev.impactor.ui.containers.views.builders;

import net.impactdev.impactor.api.ui.containers.Layout;
import net.impactdev.impactor.api.ui.containers.processors.ClickProcessor;
import net.impactdev.impactor.api.ui.containers.processors.CloseProcessor;
import net.impactdev.impactor.api.ui.containers.views.BaseViewBuilder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

public class ImpactorBaseViewBuilder<T extends BaseViewBuilder<T>> implements BaseViewBuilder<T> {

    public Key namespace;
    public Component title;
    public Layout layout;
    public boolean readonly = true;

    public ClickProcessor click = context -> true;
    public CloseProcessor close = context -> true;

    @Override
    public T provider(Key key) {
        this.namespace = key;
        return (T) this;
    }

    @Override
    public T title(Component title) {
        this.title = title;
        return (T) this;
    }

    @Override
    public T layout(Layout layout) {
        this.layout = layout;
        return (T) this;
    }

    @Override
    public T readonly(boolean state) {
        this.readonly = state;
        return (T) this;
    }

    @Override
    public T onClick(ClickProcessor processor) {
        this.click = processor;
        return (T) this;
    }

    @Override
    public T onClose(CloseProcessor processor) {
        this.close = processor;
        return (T) this;
    }
}
