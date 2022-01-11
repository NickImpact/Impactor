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

package net.impactdev.impactor.velocity.logging;

import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.velocity.VelocityImpactorBootstrap;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

public class VelocityLogger implements net.impactdev.impactor.api.logging.Logger {

    private final ImpactorPlugin plugin;

    public VelocityLogger(ImpactorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void noTag(String message) {
        VelocityImpactorBootstrap.getInstance().getProxy().getConsoleCommandSource().sendMessage(
                Identity.nil(),
                LegacyComponentSerializer.legacyAmpersand().deserialize(message),
                MessageType.SYSTEM
        );
    }

    @Override
    public void noTag(List<String> message) {
        message.forEach(this::noTag);
    }

    @Override
    public void info(String message) {
        String actual = plugin.getMetadata().getName() + " &7\u00bb " + message;

        VelocityImpactorBootstrap.getInstance().getProxy().getConsoleCommandSource().sendMessage(
                Identity.nil(),
                LegacyComponentSerializer.legacyAmpersand().deserialize(actual),
                MessageType.SYSTEM
        );
    }

    @Override
    public void info(List<String> message) {
        message.forEach(this::info);
    }


    @Override
    public void warn(String message) {
        String actual = plugin.getMetadata().getName() + " &7(&6Warning&7) " + message;

        VelocityImpactorBootstrap.getInstance().getProxy().getConsoleCommandSource().sendMessage(
                Identity.nil(),
                LegacyComponentSerializer.legacyAmpersand().deserialize(actual),
                MessageType.SYSTEM
        );
    }

    @Override
    public void warn(List<String> message) {
        message.forEach(this::warn);
    }

    @Override
    public void error(String message) {
        String actual = plugin.getMetadata().getName() + " &7(&cError&7) " + message;

        VelocityImpactorBootstrap.getInstance().getProxy().getConsoleCommandSource().sendMessage(
                Identity.nil(),
                LegacyComponentSerializer.legacyAmpersand().deserialize(actual),
                MessageType.SYSTEM
        );
    }

    @Override
    public void error(List<String> message) {
        message.forEach(this::error);
    }

    @Override
    public void debug(String message) {
        String actual = plugin.getMetadata().getName() + " &7(&bDebug&7) " + message;

        VelocityImpactorBootstrap.getInstance().getProxy().getConsoleCommandSource().sendMessage(
                Identity.nil(),
                LegacyComponentSerializer.legacyAmpersand().deserialize(actual),
                MessageType.SYSTEM
        );
    }

    public void debug(List<String> message) {
        if(this.plugin.inDebugMode()) {
            message.forEach(this::debug);
        }
    }

}
