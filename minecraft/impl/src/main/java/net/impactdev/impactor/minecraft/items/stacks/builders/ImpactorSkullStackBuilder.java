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

package net.impactdev.impactor.minecraft.items.stacks.builders;

import com.google.common.base.Preconditions;
import net.impactdev.impactor.api.items.builders.AbstractStackBuilder;
import net.impactdev.impactor.api.items.builders.provided.SkullStackBuilder;
import net.impactdev.impactor.api.items.extensions.SkullStack;
import net.impactdev.impactor.minecraft.items.stacks.providers.ImpactorSkullStack;
import org.jetbrains.annotations.Nullable;

public class ImpactorSkullStackBuilder extends AbstractStackBuilder<SkullStack, SkullStackBuilder> implements SkullStackBuilder {

    public boolean isPlayer;
    public @Nullable SkullStack.SkullType type;
    public @Nullable String texture;
    public @Nullable String target;

    @Override
    public SkullStackBuilder mob(SkullStack.SkullType type) {
        this.isPlayer = false;
        this.type = type;
        return this;
    }

    @Override
    public SkullStackBuilder player(String spec, boolean texture) {
        if(texture) {
            this.texture = spec;
        } else {
            this.target = spec;
        }

        this.isPlayer = true;
        return this;
    }

    @Override
    public SkullStack build() {
        if(this.isPlayer) {
            Preconditions.checkArgument(!(this.texture == null && this.target == null), "Missing player head parameters");
        } else {
            Preconditions.checkNotNull(this.type, "Missing Mob skull parameter");
        }

        return new ImpactorSkullStack(this);
    }
}
