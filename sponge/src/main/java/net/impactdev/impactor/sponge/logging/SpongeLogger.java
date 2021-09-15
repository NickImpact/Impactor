/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2021 NickImpact
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

package net.impactdev.impactor.sponge.logging;

import net.impactdev.impactor.api.logging.Logger;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.api.Sponge;

import java.util.List;

public class SpongeLogger implements Logger {

    private final ImpactorPlugin plugin;
    private final org.apache.logging.log4j.Logger fallback;

    public SpongeLogger(ImpactorPlugin plugin, org.apache.logging.log4j.Logger fallback) {
        this.plugin = plugin;
        this.fallback = fallback;
    }

    @Override
    public void noTag(String message) {
        if (Sponge.isServerAvailable()) {
            Sponge.server().sendMessage(Identity.nil(), this.toText(message), MessageType.SYSTEM);
        } else {
            fallback.info(message);
        }
    }

    @Override
    public void noTag(List<String> message) {
        message.forEach(this::noTag);
    }

    @Override
    public void info(String message) {
        if (Sponge.isServerAvailable()) {
            Sponge.game().systemSubject().sendMessage(
                    Identity.nil(),
                    Component.text(plugin.getMetadata().getName()).color(NamedTextColor.YELLOW)
                            .append(Component.text(" \u00bb ").color(NamedTextColor.GRAY))
                            .append(this.toText(message)),
                    MessageType.CHAT
            );
        } else {
            fallback.info(message);
        }
    }

    @Override
    public void info(List<String> message) {
        message.forEach(this::info);
    }

    @Override
    public void warn(String message) {
        if (Sponge.isServerAvailable()) {
            Sponge.game().systemSubject().sendMessage(
                    Identity.nil(),
                    Component.text(plugin.getMetadata().getName()).color(NamedTextColor.YELLOW)
                            .append(Component.text("(").color(NamedTextColor.GRAY))
                            .append(Component.text("Warning").color(NamedTextColor.GOLD))
                            .append(Component.text(")").color(NamedTextColor.GRAY))
                            .append(this.toText(message)),
                    MessageType.CHAT
            );
        } else {
            fallback.warn(message);
        }
    }

    @Override
    public void warn(List<String> message) {
        message.forEach(this::warn);
    }

    @Override
    public void error(String message) {
        if (Sponge.isServerAvailable()) {
            Sponge.game().systemSubject().sendMessage(
                    Identity.nil(),
                    Component.text(plugin.getMetadata().getName()).color(NamedTextColor.YELLOW)
                            .append(Component.text("(").color(NamedTextColor.GRAY))
                            .append(Component.text("Error").color(NamedTextColor.RED))
                            .append(Component.text(")").color(NamedTextColor.GRAY))
                            .append(this.toText(message)),
                    MessageType.CHAT
            );
        } else {
            fallback.warn(message);
        }
    }

    @Override
    public void error(List<String> message) {
        message.forEach(this::error);
    }

    @Override
    public void debug(String message) {
        if (this.plugin.inDebugMode()) {
            if (Sponge.isServerAvailable()) {
                Sponge.game().systemSubject().sendMessage(
                        Identity.nil(),
                        Component.text(plugin.getMetadata().getName()).color(NamedTextColor.YELLOW)
                                .append(Component.text("(").color(NamedTextColor.GRAY))
                                .append(Component.text("Debug").color(NamedTextColor.AQUA))
                                .append(Component.text(")").color(NamedTextColor.GRAY))
                                .append(this.toText(message)),
                        MessageType.CHAT
                );
            } else {
                fallback.warn(message);
            }
        }
    }

    @Override
    public void debug(List<String> message) {
        if (this.plugin.inDebugMode()) {
            message.forEach(this::debug);
        }
    }

    private TextComponent toText(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }
}
