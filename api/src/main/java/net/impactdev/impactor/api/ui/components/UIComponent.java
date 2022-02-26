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

package net.impactdev.impactor.api.ui.components;

import net.impactdev.impactor.api.ui.layouts.Layout;
import net.kyori.adventure.text.Component;

public interface UIComponent<T extends UIComponent<T>> {

    /**
     * Sets the title of the viewable interface to the following component.
     *
     * @param title The title of the viewable interface
     * @return The updated builder
     */
    T title(Component title);

    /**
     * Defines the layout of the UI.
     *
     * @param layout The layout that'll be used for the UI
     * @return The updated builder
     */
    T layout(Layout layout);

    /**
     * Marks whether a pagination will be considered readonly. In this state, any changes to the inventory
     * will be cancelled entirely.
     *
     * <p>By default, this is considered <code>true</code> and all changes will be cancelled. Use this
     * method if you absolutely desire that the inventory be modifiable.
     *
     * @return The updated builder
     */
    T readonly(boolean state);

}
