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

package net.impactdev.impactor.core.commands.sources;

import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.platform.sources.PlatformPlayer;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class ImpactorCommandSource implements CommandSource {

    private final PlatformSource source;

    public ImpactorCommandSource(PlatformSource source) {
        this.source = source;
    }

    @Override
    public UUID uuid() {
        return this.source.uuid();
    }

    @Override
    public Component name() {
        return this.source.name();
    }

    @Override
    public PlatformSource source() {
        return this.source;
    }

    @Override
    public PlatformPlayer player() {
        if(this.source instanceof PlatformPlayer) {
            return (PlatformPlayer) this.source;
        }

        throw new IllegalStateException("Source is not a player");
    }

    @Override
    public SourceMetadata metadata() {
        return new SourceMetadata() {
            @Override
            public boolean acceptsSuccess() {
                return true;
            }

            @Override
            public boolean acceptsFailure() {
                return true;
            }
        };
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        this.source.sendMessage(source, message, type);
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
        this.source.sendActionBar(message);
    }

    @Override
    public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
        this.source.sendPlayerListHeaderAndFooter(header, footer);
    }

    @Override
    public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
        this.source.sendTitlePart(part, value);
    }

    @Override
    public void showBossBar(@NotNull BossBar bar) {
        this.source.showBossBar(bar);
    }

    @Override
    public void hideBossBar(@NotNull BossBar bar) {
        this.source.hideBossBar(bar);
    }

    @Override
    public void clearTitle() {
        this.source.clearTitle();
    }

    @Override
    public void resetTitle() {
        this.source.resetTitle();
    }

    @Override
    public void playSound(@NotNull Sound sound) {
        this.source.playSound(sound);
    }

    @Override
    public void playSound(@NotNull Sound sound, double x, double y, double z) {
        this.source.playSound(sound, x, y, z);
    }

    @Override
    public void playSound(@NotNull Sound sound, Sound.@NotNull Emitter emitter) {
        this.source.playSound(sound, emitter);
    }

    @Override
    public void openBook(@NotNull Book book) {
        this.source.openBook(book);
    }
}