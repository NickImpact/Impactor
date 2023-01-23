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

package net.impactdev.impactor.forge.platform;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.platform.PlatformComponent;
import net.impactdev.impactor.api.platform.PlatformType;
import net.impactdev.impactor.api.platform.plugins.PluginMetadata;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.platform.ImpactorPlatformInfo;
import net.impactdev.impactor.forge.platform.components.ForgeComponent;
import net.impactdev.impactor.forge.platform.components.ForgeMinecraftComponent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ForgePlatformInfo extends ImpactorPlatformInfo {

    private final List<String> exclusions = Lists.newArrayList("minecraft", "forge");

    protected ForgePlatformInfo() {
        super(PlatformType.FORGE);
    }

    @Override
    protected void printComponents(PrettyPrinter printer) {
        printer.add("Components:");
        for(PlatformComponent component : this.components()) {
            printer.add(component);
        }

        printer.hr('-').add("Mods: ");
        List<ModInfo> mods = ModList.get().getMods()
                .stream()
                .filter(info -> !this.exclusions.contains(info.getModId()))
                .collect(Collectors.toList());
        for(IModInfo info : mods) {
            printer.add("%s - %s", info.getDisplayName(), info.getVersion());
        }
    }

    @Override
    protected void specifyComponents(Set<PlatformComponent> set) {
        set.add(new ForgeMinecraftComponent());
        set.add(new ForgeComponent());
    }

    @Override
    public List<PluginMetadata> plugins() {
        return ModList.get().getMods()
                .stream()
                .map(this::translate)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PluginMetadata> plugin(String id) {
        return ModList.get().getModContainerById(id)
                .map(ModContainer::getModInfo)
                .map(this::translate);
    }

    private PluginMetadata translate(IModInfo info) {
        return PluginMetadata.builder()
                .id(info.getModId())
                .name(info.getDisplayName())
                .version(info.getVersion().toString())
                .description(info.getDescription())
                .build();
    }
}
