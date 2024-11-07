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

package net.impactdev.impactor.core.economy.networking.consumption;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonElement;
import net.impactdev.impactor.core.economy.networking.messages.Message;
import org.jetbrains.annotations.NotNull;

public interface MessageConsumer {

    /**
     * Consumes a message instance.
     *
     * <p>The return type indicates if the platform accepted the message. It is expected
     * for implementations to return <code>false</code> if a message with an already
     * received ID has been processed.</p>
     *
     * @param message The message being consumed
     */
    @CanIgnoreReturnValue
    void consume(final @NotNull Message message);

    /**
     * Consumes a message via a relative json element.
     *
     * <p>This method will be invoked if the message being consumed was published using
     * {@link Message#serialized()}.</p>
     *
     * @param json The encoding that should be parsed
     */
    @CanIgnoreReturnValue
    void consume(final @NotNull JsonElement json);

}
