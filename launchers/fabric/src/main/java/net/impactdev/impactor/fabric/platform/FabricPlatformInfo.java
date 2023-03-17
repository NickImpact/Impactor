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

package net.impactdev.impactor.fabric.platform;

import com.google.common.collect.Lists;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.impactdev.impactor.fabric.platform.components.FabricComponent;
import net.impactdev.impactor.fabric.platform.components.FabricMinecraftComponent;
import net.impactdev.impactor.api.platform.PlatformComponent;
import net.impactdev.impactor.api.platform.PlatformType;
import net.impactdev.impactor.api.platform.plugins.PluginMetadata;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.platform.ImpactorPlatformInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class FabricPlatformInfo extends ImpactorPlatformInfo {

    private final List<String> exclusions = Lists.newArrayList("minecraft", "fabric");

    FabricPlatformInfo() {
        super(PlatformType.FABRIC);
    }

    @Override
    public List<PluginMetadata> plugins() {
        return FabricLoader.getInstance().getAllMods()
                .stream()
                .map(ModContainer::getMetadata)
                .map(this::translate)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PluginMetadata> plugin(String id) {
        return FabricLoader.getInstance()
                .getModContainer(id)
                .map(ModContainer::getMetadata)
                .map(this::translate);
    }

    @Override
    protected void printComponents(PrettyPrinter printer) {
        printer.add("Components:");
        for(PlatformComponent component : this.components()) {
            printer.add(component);
        }

        printer.hr('-').add("Mods: ");
        List<ModContainer> mods = FabricLoader.getInstance().getAllMods()
                .stream()
                .filter(info -> !this.exclusions.contains(info.getMetadata().getId()))
                .collect(Collectors.toList());
        for(ModContainer info : mods) {
            printer.add("%s - %s", info.getMetadata().getName(), info.getMetadata().getVersion().getFriendlyString());
        }
    }

    @Override
    protected void specifyComponents(Set<PlatformComponent> set) {
        set.add(new FabricMinecraftComponent());
        set.add(new FabricComponent());
    }

    private PluginMetadata translate(ModMetadata metadata) {
        return PluginMetadata.builder()
                .id(metadata.getId())
                .name(metadata.getName())
                .version(metadata.getVersion().getFriendlyString())
                .description(metadata.getDescription())
                .build();
    }
}
