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

package net.impactdev.impactor.spigot.platform;

import net.impactdev.impactor.api.platform.PlatformComponent;
import net.impactdev.impactor.api.platform.PlatformType;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.platform.ImpactorPlatformInfo;
import net.impactdev.impactor.spigot.platform.components.SpigotComponent;
import net.impactdev.impactor.spigot.platform.components.SpigotMinecraftComponent;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Set;

public class SpigotPlatformInfo extends ImpactorPlatformInfo {

    protected SpigotPlatformInfo() {
        super(PlatformType.SPIGOT);
    }

    @Override
    protected void printComponents(PrettyPrinter printer) {
        printer.add("Components:");
        for(PlatformComponent component : this.components()) {
            printer.add(component);
        }

        printer.hr('-').add("Plugins: ");
        Arrays.stream(Bukkit.getPluginManager().getPlugins()).forEach(plugin -> {
            printer.add("%s - %s", plugin.getName(), plugin.getDescription().getVersion());
        });
    }

    @Override
    protected void specifyComponents(Set<PlatformComponent> set) {
        set.add(new SpigotMinecraftComponent());
        set.add(new SpigotComponent());
    }
}
