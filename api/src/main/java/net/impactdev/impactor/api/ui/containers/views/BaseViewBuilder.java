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

package net.impactdev.impactor.api.ui.containers.views;

import net.impactdev.impactor.api.builders.Required;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.processors.ClickProcessor;
import net.impactdev.impactor.api.ui.containers.processors.CloseProcessor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;

public interface BaseViewBuilder<T extends BaseViewBuilder<T>> {

    /**
     * Sets the key referencing the provider of this pagination. This key provides both a namespace
     * and a value to identity the type of pagination, as well as a means of reference to a particular
     * pagination in the event an error occurs during its processing.
     *
     * @param key The key containing the namespace and value information of a provider.
     * @return The updated builder
     */
    @Required
    @Contract("_ -> this")
    T provider(Key key);

    /**
     * Sets the title of the viewable interface to the following component.
     *
     * @param title The title of the viewable interface
     * @return The updated builder
     */
    @Contract("_ -> this")
    T title(Component title);

    /**
     * Marks whether a pagination will be considered readonly. In this state, any changes to the inventory
     * will be cancelled entirely.
     *
     * <p>By default, this is considered <code>true</code> and all changes will be cancelled. Use this
     * method if you absolutely desire that the inventory be modifiable.
     *
     * @return The updated builder
     */
    @Contract("_ -> this")
    T readonly(boolean state);

    /**
     * Registers a universal click processor that applies to the entire container. This is different
     * from an {@link Icon icon's} set of listeners, and is meant to cover cases outside normal icon listening.
     *
     * @param processor A processor for handling global click events
     * @return The updated builder
     */
    @Contract("_ -> this")
    T onClick(ClickProcessor processor);

    /**
     * Registers a close handler for the UI that is capable of controlling the close of the UI.
     * If the UI is closed, but the call to this handler returns false, the action will be denied,
     * and the UI will be reopened to the client.
     *
     * @param processor The handler for the action
     * @return The updated builder
     */
    @Contract("_ -> this")
    T onClose(CloseProcessor processor);

}
