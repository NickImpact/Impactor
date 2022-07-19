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

package net.impactdev.impactor.sponge.text;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.services.parsing.PlaceholderParser;
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
import org.spongepowered.api.placeholder.PlaceholderContext;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class SpongePlaceholderParser extends PlaceholderParser {

    public SpongePlaceholderParser(String raw, String placeholder, String arguments, PlaceholderSources sources) {
        super(raw, placeholder, arguments, sources);
    }

    private Optional<org.spongepowered.api.placeholder.PlaceholderParser> getParser(String key) {
        return RegistryTypes.PLACEHOLDER_PARSER.get()
                .stream()
                .filter(parser -> parser.key(RegistryTypes.PLACEHOLDER_PARSER).formatted().equals(key))
                .findAny();
    }

    @Override
    public List<Component> components() {
        Optional<org.spongepowered.api.placeholder.PlaceholderParser> parser = this.getParser(this.value);
        Component x = parser.map(p -> {
            Component result = p.parse(PlaceholderContext.builder()
                    .associatedObject(this.sources)
                    .argumentString(this.arguments)
                    .build()
            );

            if (result != Component.empty()) {
                return result;
            }

            for(Supplier<?> supplier : sources.suppliers()) {
                Component attempt = p.parse(PlaceholderContext.builder().associatedObject(supplier).argumentString(arguments).build());
                if(attempt != Component.empty()) {
                    return attempt;
                }
            }

            return Component.text(this.raw);
        }).orElse(Component.empty());

        return this.separate(x);
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
