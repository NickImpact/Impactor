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

package net.impactdev.impactor.core.economy.registration;

import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.platform.PluginMetadata;
import net.impactdev.impactor.core.economy.ImpactorEconomyService;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;

import java.util.Optional;
import java.util.function.Supplier;

public final class EconomyRegistrationProvider {

    private EconomySuggestion suggestion;

    public EconomyRegistrationProvider() {
        this.suggest(BaseImpactorPlugin.instance().metadata(), 0, ImpactorEconomyService::new);
    }

    public EconomySuggestion suggestion() {
        return Optional.ofNullable(this.suggestion).orElseThrow(() -> new IllegalStateException("No suggestions available"));
    }

    public void suggest(PluginMetadata metadata, int priority, Supplier<EconomyService> supplier) {
        if(this.suggestion == null || this.suggestion.priority < priority) {
            this.suggestion = this.createSuggestion(metadata, priority, supplier);
        }
    }

    private EconomySuggestion createSuggestion(PluginMetadata metadata, int priority, Supplier<EconomyService> supplier) {
        return new EconomySuggestion(
                metadata,
                priority,
                supplier
        );
    }

    public static final class EconomySuggestion {

        private final PluginMetadata metadata;
        private final int priority;
        private final Supplier<EconomyService> supplier;

        private EconomySuggestion(PluginMetadata metadata, int priority, Supplier<EconomyService> supplier) {
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

        public Supplier<EconomyService> supplier() {
            return this.supplier;
        }
    }

}
