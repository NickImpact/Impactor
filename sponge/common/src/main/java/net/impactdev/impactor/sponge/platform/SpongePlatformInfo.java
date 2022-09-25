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

package net.impactdev.impactor.sponge.platform;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.platform.PlatformComponent;
import net.impactdev.impactor.api.platform.PlatformType;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.platform.ImpactorPlatformInfo;
import net.impactdev.impactor.sponge.platform.components.SpongeAPIComponent;
import net.impactdev.impactor.sponge.platform.components.SpongeImplementationComponent;
import net.impactdev.impactor.sponge.platform.components.SpongeMinecraftComponent;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

import java.util.List;
import java.util.Set;

public class SpongePlatformInfo extends ImpactorPlatformInfo {

    private final List<String> exclusions = Lists.newArrayList(
            "minecraft", "forge", "spongeapi", "sponge", "spongevanilla", "spongeforge"
    );

    protected SpongePlatformInfo() {
        super(PlatformType.SPONGE);
    }

    @Override
    protected void printComponents(PrettyPrinter printer) {
        printer.add("Components:");
        for(PlatformComponent component : this.components()) {
            printer.add(component);
        }

        printer.hr('-').add("Mods/Plugins: ");
        Sponge.pluginManager().plugins()
                .stream()
                .map(PluginContainer::metadata)
                .filter(info -> !this.exclusions.contains(info.id()))
                .forEach(meta -> {
                    printer.add("%s - %s", meta.name().orElse(meta.id()), meta.version().toString());
                });
    }

    @Override
    protected void specifyComponents(Set<PlatformComponent> set) {
        set.add(new SpongeMinecraftComponent());
        set.add(new SpongeAPIComponent());
        set.add(new SpongeImplementationComponent());
    }

}
