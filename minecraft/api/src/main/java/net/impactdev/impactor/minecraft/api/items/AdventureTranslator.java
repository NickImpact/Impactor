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

package net.impactdev.impactor.minecraft.api.items;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.services.Service;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

public interface AdventureTranslator extends Service {

    static AdventureTranslator get() {
        return Impactor.instance().services().provide(AdventureTranslator.class);
    }

    interface Server {

        static AdventureTranslator.Server get(MinecraftServer server) {
            return Impactor.instance().factories().provide(Factory.class).create(server);
        }

        Component asAdventure(net.minecraft.network.chat.Component component);

        net.minecraft.network.chat.Component asNative(Component component);

        interface Factory {

            AdventureTranslator.Server create(MinecraftServer server);

        }

    }

    Key asAdventure(ResourceLocation location);

    ResourceLocation asNative(Key key);

}
