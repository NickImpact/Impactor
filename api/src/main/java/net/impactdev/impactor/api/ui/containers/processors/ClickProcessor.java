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

package net.impactdev.impactor.api.ui.containers.processors;

import net.impactdev.impactor.api.utilities.context.Context;

@FunctionalInterface
public interface ClickProcessor {

    /**
     * Attempts to process an event given the respective context. The context can be built up of
     * individual values, and in some cases, the components of an event. Typically, you'll find
     * at least the platform's player instance provided at a minimum.
     *
     * <p>This method allows a boolean value to be processed to indicate whether an inventory
     * interaction should be allowed. The actual icon that inherits its set of listeners will
     * query all listeners for a single false value. If any false value is set, the allowance
     * of an interaction will be denied. Additionally, the view can override a situation
     * where all listeners allow the change, if the view itself is set to a readonly state.
     *
     * @param context Contextual information regarding the event being processed by the implementation
     * @return <code>true</code> if the processor should allow item movement, <code>false</code> otherwise
     * @throws RuntimeException If any error occurs during event processing
     */
    boolean process(Context context) throws RuntimeException;

}
