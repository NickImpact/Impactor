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

package net.impactdev.impactor.platform;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.impactdev.impactor.api.platform.PlatformComponent;
import net.impactdev.impactor.api.platform.PlatformInfo;
import net.impactdev.impactor.api.platform.PlatformType;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.platform.components.JDKPlatformComponent;

import java.util.Set;

public abstract class ImpactorPlatformInfo implements PlatformInfo {

    private final PlatformType type;
    private final Set<PlatformComponent> components;

    protected ImpactorPlatformInfo(PlatformType type) {
        this.type = type;
        this.components = this.generate();
    }

    @Override
    public PlatformType type() {
        return this.type;
    }

    @Override
    public Set<PlatformComponent> components() {
        return ImmutableSet.copyOf(this.components);
    }

    @Override
    public void print(PrettyPrinter printer) {
        printer.add("Platform: " + this.type.name().charAt(0) + this.type.name().substring(1).toUpperCase());
        printer.newline();
        this.printComponents(printer);
    }

    private Set<PlatformComponent> generate() {
        Set<PlatformComponent> result = Sets.newLinkedHashSet();
        result.add(new JDKPlatformComponent());
        this.specifyComponents(result);

        return result;
    }

    protected abstract void printComponents(PrettyPrinter printer);

    protected abstract void specifyComponents(Set<PlatformComponent> set);
}
