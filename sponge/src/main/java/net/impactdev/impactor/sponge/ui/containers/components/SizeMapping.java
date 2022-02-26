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

package net.impactdev.impactor.sponge.ui.containers.components;

import org.spongepowered.api.item.inventory.ContainerType;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.registry.DefaultedRegistryReference;

public enum SizeMapping {
    One(ContainerTypes.GENERIC_9X1),
    Two(ContainerTypes.GENERIC_9X2),
    Three(ContainerTypes.GENERIC_9X3),
    Four(ContainerTypes.GENERIC_9X4),
    Five(ContainerTypes.GENERIC_9X5),
    Six(ContainerTypes.GENERIC_9X6);

    private final DefaultedRegistryReference<ContainerType> type;

    SizeMapping(DefaultedRegistryReference<ContainerType> type) {
        this.type = type;
    }

    public static SizeMapping from(int rows) {
        return SizeMapping.values()[rows - 1];
    }

    public DefaultedRegistryReference<ContainerType> reference() {
        return this.type;
    }
}