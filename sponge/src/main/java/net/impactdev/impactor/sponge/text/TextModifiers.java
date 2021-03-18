/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package net.impactdev.impactor.sponge.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.function.Function;

public enum TextModifiers implements Function<Component, Component> {

    SPACE_AFTER("s") {
        @Override
        public Component apply(Component text) {
            return text.append(Component.space());
        }
    },
    SPACE_BEFORE("p") {
        @Override
        public Component apply(Component text) {
            return Component.space().append(text);
        }
    };

    private final String key;

    TextModifiers(String p) {
        this.key = p;
    }

    public String getKey() {
        return this.key;
    }
}
