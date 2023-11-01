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

package net.impactdev.impactor.integrations.octo.fabric;

import com.epherical.octoecon.api.event.EconomyEvents;
import com.google.common.base.Suppliers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.economy.events.SuggestEconomyServiceEvent;
import net.impactdev.impactor.api.platform.plugins.PluginMetadata;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.integrations.octo.fabric.config.OctoConfig;
import net.impactdev.impactor.integrations.octo.fabric.mirrors.ImpactorToOctoMirror;
import net.impactdev.impactor.integrations.octo.fabric.mirrors.OctoToImpactorMirror;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

public final class FabricOctoIntegration implements ModInitializer {

    @Override
    public void onInitialize() {
        PluginMetadata metadata = PluginMetadata.builder()
                .id("impactor-octo")
                .name("Impactor OctoEconomyAPI Mirror")
                .version("@version@")
                .build();

        Config config = Config.builder()
                .path(BaseImpactorPlugin.instance().configurationDirectory().resolve("integrations").resolve("octo.conf"))
                .provider(OctoConfig.class)
                .provideIfMissing(() -> this.resource(root -> root.resolve("octo.conf")))
                .build();

        if(config.get(OctoConfig.USE_IMPACTOR)) {
            OctoToImpactorMirror mirror = new OctoToImpactorMirror();
            ServerLifecycleEvents.SERVER_STARTING.register(event -> {
                EconomyEvents.ECONOMY_CHANGE_EVENT.invoker().onEconomyChanged(mirror);
            });
        } else {
            var mirror = new ImpactorToOctoMirror();
            Impactor.instance().events().subscribe(
                    SuggestEconomyServiceEvent.class,
                    event -> event.suggest(metadata, Suppliers.memoize(() -> mirror), 1)
            );
        }
    }

    private InputStream resource(Function<Path, Path> target) {
        Path path = target.apply(Paths.get("impactor-octo").resolve("assets"));
        return Optional.ofNullable(this.getClass().getClassLoader().getResourceAsStream(path.toString().replace("\\", "/")))
                .orElseThrow(() -> new IllegalArgumentException("Target resource not located"));
    }
}
