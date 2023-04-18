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

package net.impactdev.impactor.core.platform.sources;

import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.platform.sources.SourceType;
import net.impactdev.impactor.api.platform.sources.metadata.MetadataKey;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class ImpactorPlatformSource implements PlatformSource {

    private final UUID uuid;
    private final SourceType type;
    private final Map<MetadataKey<?>, Supplier<?>> metadata;

    public ImpactorPlatformSource(UUID uuid, SourceType type) {
        this.uuid = uuid;
        this.type = type;
        this.metadata = new HashMap<>();
    }

    @Override
    public Locale locale() {
        return Locale.getDefault();
    }

    @Override
    public UUID uuid() {
        return this.uuid;
    }

    @Override
    public SourceType type() {
        return this.type;
    }

    @Override
    public Component name() {
        if(this.type == SourceType.SERVER) {
            return Component.text("Server");
        }

        return Component.empty();
    }

    @Override
    public <T> Optional<T> metadata(MetadataKey<T> key) {
        return Optional.ofNullable(this.metadata.get(key)).map(supplier -> (T) supplier.get());
    }

    @Override
    public <T> void offer(MetadataKey<T> key, Supplier<T> instance) {
        if(instance == null) {
            this.metadata.remove(key);
            return;
        }

        this.metadata.put(key, instance);
    }

}
