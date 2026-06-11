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

package net.impactdev.impactor.forge;

import net.impactdev.impactor.minecraft.api.items.AdventureTranslator;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.modcommon.MinecraftAudiences;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.kyori.adventure.text.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

public class NeoforgeAdventureTranslator implements AdventureTranslator {

    @Override
    public Key asAdventure(Identifier location) {
        return MinecraftAudiences.asAdventure(location);
    }

    @Override
    public Identifier asNative(Key key) {
        return MinecraftAudiences.asNative(key);
    }

    @Override
    public String name() {
        return "Neoforge Adventure Platform Translator";
    }

    public record NeoforgeServerTranslator(MinecraftServerAudiences translator) implements AdventureTranslator.Server {

        @Override
        public Component asAdventure(net.minecraft.network.chat.Component component) {
            return this.translator.asAdventure(component);
        }

        @Override
        public net.minecraft.network.chat.Component asNative(Component component) {
            return this.translator.asNative(component);
        }

    }

    public static final class NeoforgeServerTranslatorFactory implements AdventureTranslator.Server.Factory {

        @Override
        public Server create(MinecraftServer server) {
            return new NeoforgeServerTranslator(MinecraftServerAudiences.of(server));
        }

    }
}
