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

package net.impactdev.impactor.test.dummies;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.platform.Platform;
import net.impactdev.impactor.api.platform.PlatformComponent;
import net.impactdev.impactor.api.platform.PlatformInfo;
import net.impactdev.impactor.api.platform.PlatformType;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.players.PlatformPlayerService;
import net.impactdev.impactor.api.platform.plugins.PluginMetadata;
import net.impactdev.impactor.api.providers.ServiceProvider;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.impactdev.impactor.core.platform.ImpactorPlatform;
import net.impactdev.impactor.core.platform.ImpactorPlatformInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class TestPlatform extends ImpactorPlatform {
    public TestPlatform() {
        super(new TestPlatformInfo(PlatformType.JUNIT_TESTING));
    }

    public static class TestPlatformInfo extends ImpactorPlatformInfo {

        protected TestPlatformInfo(PlatformType type) {
            super(type);
        }

        @Override
        public List<PluginMetadata> plugins() {
            return Lists.newArrayList();
        }

        @Override
        public Optional<PluginMetadata> plugin(String id) {
            return Optional.empty();
        }

        @Override
        protected void printComponents(PrettyPrinter printer) {
            printer.add("Components:");
            for(PlatformComponent component : this.components()) {
                printer.add(component);
            }
        }

        @Override
        protected void specifyComponents(Set<PlatformComponent> set) {

        }
    }

    public static class TestPlatformModule implements ImpactorModule {

        @Override
        public void services(ServiceProvider provider) {
            provider.register(Platform.class, new TestPlatform());
            provider.register(PlatformPlayerService.class, new PlatformPlayerService() {
                @Override
                public PlatformPlayer getOrCreate(@NotNull UUID uuid) {
                    return PlatformPlayer.getOrCreate(uuid);
                }

                @Override
                public Set<PlatformPlayer> online() {
                    return Collections.emptySet();
                }
            });
        }
    }
}
