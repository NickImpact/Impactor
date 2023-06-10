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
import net.fabricmc.loader.impl.discovery.ModCandidate;
import net.impactdev.impactor.fabric.platform.components.FabricComponent;
import net.impactdev.impactor.fabric.platform.components.FabricMinecraftComponent;
import net.impactdev.impactor.api.platform.PlatformComponent;
import net.impactdev.impactor.api.platform.PlatformType;
import net.impactdev.impactor.api.platform.plugins.PluginMetadata;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.platform.ImpactorPlatformInfo;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class FabricPlatformInfo extends ImpactorPlatformInfo {

    private final List<String> exclusions = Lists.newArrayList(
            "minecraft",
            "fabricloader",
            "java"
    );

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
        printer.table("Name", "Version");
        for(PlatformComponent component : this.components()) {
            printer.tr(component.name(), component.version());
        }
        printer.hr('-');

        List<ModContainer> mods = FabricLoader.getInstance().getAllMods()
                .stream()
                .filter(info -> !this.exclusions.contains(info.getMetadata().getId()))
                .filter(info -> info.getContainingMod().isEmpty())
                .collect(Collectors.toList());
        printer.newline().add("Mods (%d)", (Object) mods.size());
        mods.forEach(mod -> this.printModContainer(mod, printer, 0, false));

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

    private void printModContainer(ModContainer target, PrettyPrinter printer, int level, boolean last) {
        StringBuilder builder = new StringBuilder();
        for(int depth = 0; depth < level; depth++) {
            builder.append(depth == 0 ? "\t" : "   | ");
        }

        builder.append(level == 0 ? "\t" : "  ");
        builder.append(level == 0 ? "-" : last ? " \\--" : " |--");
        builder.append(' ').append(target.getMetadata().getId()).append(' ');
        builder.append(target.getMetadata().getVersion().getFriendlyString());

        printer.add(builder.toString());

        List<ModContainer> nestedMods = new ArrayList<>(target.getContainedMods());
        nestedMods.sort(Comparator.comparing(nestedMod -> nestedMod.getMetadata().getId()));

        if(!nestedMods.isEmpty()) {
            Iterator<ModContainer> iterator = nestedMods.iterator();
            while(iterator.hasNext()) {
                ModContainer next = iterator.next();

                this.printModContainer(next, printer, level + 1, !iterator.hasNext());
            }
        }
    }
}
