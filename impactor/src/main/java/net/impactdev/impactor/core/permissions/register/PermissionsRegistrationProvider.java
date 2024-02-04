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

package net.impactdev.impactor.core.permissions.register;

import net.impactdev.impactor.api.platform.plugins.PluginMetadata;
import net.impactdev.impactor.api.services.permissions.PermissionsService;

import java.util.Optional;
import java.util.function.Supplier;

public final class PermissionsRegistrationProvider {

    private PermissionServiceSuggestion suggestion;

    public PermissionServiceSuggestion suggestion() {
        return Optional.ofNullable(this.suggestion).orElseThrow(() -> new IllegalStateException("No suggestions available"));
    }

    public void suggest(PluginMetadata metadata, int priority, Supplier<PermissionsService> supplier) {
        if(this.suggestion == null || this.suggestion.priority() < priority) {
            this.suggestion = this.createSuggestion(metadata, priority, supplier);
        }
    }

    private PermissionServiceSuggestion createSuggestion(PluginMetadata metadata, int priority, Supplier<PermissionsService> supplier) {
        return new PermissionServiceSuggestion(
                metadata,
                priority,
                supplier
        );
    }

    public static final class PermissionServiceSuggestion {

        private final PluginMetadata metadata;
        private final int priority;
        private final Supplier<PermissionsService> supplier;

        private PermissionServiceSuggestion(PluginMetadata metadata, int priority, Supplier<PermissionsService> supplier) {
            this.metadata = metadata;
            this.priority = priority;
            this.supplier = supplier;
        }

        public PluginMetadata metadata() {
            return this.metadata;
        }

        public int priority() {
            return this.priority;
        }

        public Supplier<PermissionsService> supplier() {
            return this.supplier;
        }
    }

}
