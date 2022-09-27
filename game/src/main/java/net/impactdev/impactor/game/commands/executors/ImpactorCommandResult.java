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

package net.impactdev.impactor.game.commands.executors;

import net.impactdev.impactor.api.commands.executors.CommandResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ImpactorCommandResult implements CommandResult {

    private final boolean successful;
    private final int result;
    private final @Nullable Throwable reason;

    public ImpactorCommandResult(boolean successful, int result, @Nullable Throwable reason) {
        this.successful = successful;
        this.result = result;
        this.reason = reason;
    }

    @Override
    public boolean isSuccessful() {
        return this.successful;
    }

    @Override
    public int result() {
        return this.result;
    }

    @Override
    public Optional<Throwable> reason() {
        return Optional.ofNullable(this.reason);
    }

    public static class ImpactorCommandResultFactory implements Factory {

        @Override
        public CommandResult successful() {
            return new ImpactorCommandResult(true, 1, null);
        }

        @Override
        public CommandResult exceptional(Throwable throwable) {
            return new ImpactorCommandResult(false, 0, throwable);
        }

    }

    public static class ImpactorCommandResultBuilder implements CommandResultBuilder {

        private boolean successful = true;
        private int result;
        private Throwable reason;

        @Override
        public CommandResultBuilder result(int result) {
            this.result = result;
            return this;
        }

        @Override
        public CommandResultBuilder exceptional(Throwable cause) {
            this.successful = false;
            this.reason = cause;
            return this;
        }

        @Override
        public CommandResult build() {
            return new ImpactorCommandResult(this.successful, this.result, this.reason);
        }

    }

}
