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

package net.impactdev.impactor.api.services.parsing;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.BlockNBTComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.EntityNBTComponent;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericTextParser implements StringParser {

    private static final Pattern STYLE_SPLITTER = Pattern.compile("(?<styles>(&[a-f\\dklmnor])*)(?<text>.+?(?=&[a-f\\dklmnor]))");
    private static final Pattern STYLE_LOCATOR = Pattern.compile("&(?<id>[a-f\\dklmnor])");
    private static final Pattern ONLY_STYLES = Pattern.compile("^(&(?<id>[a-f\\dklmnor]))+$");
    private final String content;

    public GenericTextParser(String content) {
        this.content = content;
    }

    @Override
    public String value() {
        return this.content;
    }

    @Override
    public List<Component> components() {
        Component translated = LegacyComponentSerializer.legacyAmpersand().deserialize(this.value());
        return this.separate(translated);
    }

    @Override
    public String toString() {
        return "Text: " + this.value();
    }

    private static final BiMap<Character, TextColor> ID_TO_COLOR =
            HashBiMap.create(
                    ImmutableMap.<Character, TextColor>builder()
                            .put('0', NamedTextColor.BLACK)
                            .put('1', NamedTextColor.DARK_BLUE)
                            .put('2', NamedTextColor.DARK_GREEN)
                            .put('3', NamedTextColor.DARK_AQUA)
                            .put('4', NamedTextColor.DARK_RED)
                            .put('5', NamedTextColor.DARK_PURPLE)
                            .put('6', NamedTextColor.GOLD)
                            .put('7', NamedTextColor.GRAY)
                            .put('8', NamedTextColor.DARK_GRAY)
                            .put('9', NamedTextColor.BLUE)
                            .put('a', NamedTextColor.GREEN)
                            .put('b', NamedTextColor.AQUA)
                            .put('c', NamedTextColor.RED)
                            .put('d', NamedTextColor.LIGHT_PURPLE)
                            .put('e', NamedTextColor.YELLOW)
                            .put('f', NamedTextColor.WHITE)
                            .build()
            );
    private static final BiMap<Character, Style> ID_TO_STYLE =
            HashBiMap.create(
                    ImmutableMap.<Character, Style>builder()
                            .put('l', Style.style(TextDecoration.BOLD))
                            .put('o', Style.style(TextDecoration.ITALIC))
                            .put('n', Style.style(TextDecoration.UNDERLINED))
                            .put('m', Style.style(TextDecoration.STRIKETHROUGH))
                            .put('k', Style.style(TextDecoration.OBFUSCATED))
                            .put('r', Style.empty())
                            .build()
            );

    private Style parseStyle(@Nullable String in) {
        Style style = Style.empty();
        if(in == null) {
            return style;
        }

        Matcher matcher = STYLE_LOCATOR.matcher(in);
        while(matcher.find()) {
            char match = matcher.group("id").charAt(0);
            if(ID_TO_COLOR.containsKey(match)) {
                style = style.color(ID_TO_COLOR.get(match));
            } else {
                if(ID_TO_STYLE.containsKey(match)) {
                    style = style.merge(
                            ID_TO_STYLE.get(match),
                            match == 'r' ? Style.Merge.Strategy.ALWAYS : Style.Merge.Strategy.IF_ABSENT_ON_TARGET
                    );
                }
            }
        }

        return style;
    }

    private List<Component> separate(Component parent) {
        List<Component> results = Lists.newArrayList();
        if(parent.children().size() > 0) {
            List<Component> children = parent.children();
            results.add(this.translate(parent).asComponent());

            for(Component child : children) {
                results.addAll(this.separate(child));
            }
        } else {
            results.add(parent);
        }

        return results;
    }

    private ComponentLike translate(Component in) {
        if(in instanceof TextComponent) {
            TextComponent tc = (TextComponent) in;
            return Component.text().content(tc.content()).style(in.style());
        } else if(in instanceof TranslatableComponent) {
            TranslatableComponent tc = (TranslatableComponent) in;
            return Component.translatable().key(tc.key()).style(in.style());
        } else if(in instanceof KeybindComponent) {
            KeybindComponent x = (KeybindComponent) in;
            return Component.keybind().keybind(x.keybind()).style(x.style());
        } else if(in instanceof ScoreComponent) {
            ScoreComponent x = (ScoreComponent) in;
            return Component.score().name(x.name()).objective(x.objective()).style(x.style());
        } else if(in instanceof SelectorComponent) {
            SelectorComponent x = (SelectorComponent) in;
            return Component.selector().pattern(x.pattern()).separator(x.separator()).style(x.style());
        } else if(in instanceof BlockNBTComponent) {
            BlockNBTComponent x = (BlockNBTComponent) in;
            return Component.blockNBT()
                    .nbtPath(x.nbtPath())
                    .interpret(x.interpret())
                    .separator(x.separator())
                    .pos(x.pos())
                    .style(x.style());
        } else if(in instanceof EntityNBTComponent) {
            EntityNBTComponent x = (EntityNBTComponent) in;
            return Component.entityNBT()
                    .nbtPath(x.nbtPath())
                    .interpret(x.interpret())
                    .separator(x.separator())
                    .selector(x.selector())
                    .style(x.style());
        } else if(in instanceof StorageNBTComponent) {
            StorageNBTComponent x = (StorageNBTComponent) in;
            return Component.storageNBT()
                    .nbtPath(x.nbtPath())
                    .interpret(x.interpret())
                    .separator(x.separator())
                    .storage(x.storage())
                    .style(x.style());
        }

        throw new IllegalArgumentException("Unrecognized component typing: " + in.getClass());
    }
}
