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

package net.impactdev.impactor.placeholders.provided;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformSource;
import net.impactdev.impactor.api.platform.performance.Memory;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.server.dedicated.DedicatedServer;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

public final class ImpactorPlaceholders {

    public static final ImpactorPlaceholder NAME = new ImpactorPlaceholder(
            impactor("name"),
            ctx -> ctx.request(PlatformSource.class)
                    .map(PlatformSource::name)
                    .orElse(empty())
    );

    public static final ImpactorPlaceholder UUID = new ImpactorPlaceholder(
            impactor("uuid"),
            ctx -> ctx.request(PlatformSource.class)
                    .map(PlatformSource::uuid)
                    .map(id -> text(id.toString()))
                    .orElse(empty())
    );

    public static final ImpactorPlaceholder TPS = new ImpactorPlaceholder(
            impactor("tps"),
            ctx -> text(Impactor.instance().platform().performance().ticksPerSecond())
    );
    public static final ImpactorPlaceholder MSPT = new ImpactorPlaceholder(
            impactor("mspt"),
            ctx -> text(Impactor.instance().platform().performance().averageTickDuration())
    );

    public static final ImpactorPlaceholder MEMORY_USAGE = new ImpactorPlaceholder(
            impactor("memory_used"),
            ctx -> text(Impactor.instance().platform().performance().memory().current())
    );
    public static final ImpactorPlaceholder MEMORY_ALLOCATED = new ImpactorPlaceholder(
            impactor("memory_allocated"),
            ctx -> text(Impactor.instance().platform().performance().memory().allocated())
    );
    public static final ImpactorPlaceholder MEMORY_TOTAL = new ImpactorPlaceholder(
            impactor("memory_total"),
            ctx -> text(Impactor.instance().platform().performance().memory().max())
    );

    private static Key impactor(@Subst("dummy") @Pattern("[a-z0-9_\\-./]+") String key) {
        return Key.key("impactor", key);
    }

}
