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

package net.impactdev.impactor.core.configuration;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.configuration.ConfigKey;
import net.impactdev.impactor.api.configuration.ConfigurationAdapter;
import net.impactdev.impactor.api.configuration.keys.BaseConfigKey;
import net.impactdev.impactor.api.configuration.keys.EnduringKey;
import net.impactdev.impactor.api.configuration.loader.KeyLoader;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class ImpactorConfig implements Config {

    /**
     * The configurations loaded values.
     *
     * <p>The value corresponding to each key is stored at the index defined
     * by {@link ConfigKey.ParentContext#ordinal()}.</p>
     */
    private Object[] values = null;

    private final ConfigurationAdapter adapter;
    private final KeyLoader loader;

    public ImpactorConfig(Path path, boolean supply, List<Class<?>> providers) {
        this.adapter = new ImpactorConfigurationAdapter(path, supply);
        this.loader = new KeyLoader(providers);
        this.load();
    }

    @Override
    public <T> T get(ConfigKey<T> key) {
        if(!key.context().parent().equals(this)) {
            throw new NoSuchElementException("Key does not belong to given configuration");
        }

        return (T) this.values[key.context().ordinal()];
    }

    @Override
    public synchronized void load() {
        // if this is a reload operation
        boolean reload = true;

        // if values are null, must be loading for the first time
        if (this.values == null) {
            this.values = new Object[loader.size()];
            reload = false;
        }

        for (ConfigKey<?> key : loader.keys()) {
            // don't reload enduring keys.
            if (reload && key instanceof EnduringKey) {
                continue;
            }

            if(!reload) {
                ((BaseConfigKey.ParentContextBase) key.context()).parent(this);
            }

            // load the value for the key
            Object value = key.get(this.adapter);
            this.values[key.context().ordinal()] = value;
        }
    }

    @Override
    public void reload() {
        this.adapter.reload();
        this.load();
    }

    public static class ImpactorConfigBuilder implements ConfigBuilder {

        private final List<Class<?>> providers = Lists.newArrayList();
        private Path path;
        private boolean supply;

        @Override
        public ConfigBuilder providers(Class<?> provider, Class<?>... children) {
            this.providers.add(provider);
            this.providers.addAll(Arrays.asList(children));
            return this;
        }

        @Override
        public ConfigBuilder path(Path path) {
            this.path = path;
            return this;
        }

        @Override
        public ConfigBuilder supply(boolean supply) {
            this.supply = supply;
            return this;
        }

        @Override
        public Config build() {
            return new ImpactorConfig(this.path, this.supply, this.providers);
        }
    }

}
