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

package net.impactdev.impactor.api.commands.executors;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.builders.Builder;

import java.util.Optional;

public interface CommandResult {

    static CommandResult successful() {
        return Impactor.instance().factories().provide(Factory.class).successful();
    }

    static CommandResult exceptional(Throwable reason) {
        return Impactor.instance().factories().provide(Factory.class).exceptional(reason);
    }

    static CommandResultBuilder builder() {
        return Impactor.instance().builders().provide(CommandResultBuilder.class);
    }

    boolean isSuccessful();

    int result();

    Optional<Throwable> reason();

    interface Factory {

        CommandResult successful();

        CommandResult exceptional(Throwable cause);

    }

    /**
     * Represents a builder for a command result that is assumed successful. A command result is only
     * ever considered a failure if and only if {@link #exceptional(Throwable)} has been invoked, to
     * which the result will forcibly set its state to that of a failed execution.
     */
    interface CommandResultBuilder extends Builder<CommandResult> {

        CommandResultBuilder result(int result);

        CommandResultBuilder exceptional(Throwable cause);

    }

}
