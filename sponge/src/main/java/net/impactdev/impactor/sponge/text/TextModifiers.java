/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package net.impactdev.impactor.sponge.text;

import net.kyori.text.TextComponent;

import java.util.function.Function;

public enum TextModifiers implements Function<TextComponent, TextComponent> {

    SPACE_AFTER("s") {
        @Override
        public TextComponent apply(TextComponent text) {
            return text.append(TextComponent.space());
        }
    },
    SPACE_BEFORE("p") {
        @Override
        public TextComponent apply(TextComponent text) {
            return TextComponent.space().append(text);
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
