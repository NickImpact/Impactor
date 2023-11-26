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

package net.impactdev.impactor.minecraft.utility.adventure;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.processing.CommandContainer;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.utility.Context;
import net.impactdev.impactor.core.translations.internal.ImpactorTranslations;
import net.kyori.adventure.text.event.ClickCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.UUID;

@CommandContainer
public final class ClickCallbackCommand {

    public static final String COMMAND_ID = "impactor-callback";

    @CommandMethod(COMMAND_ID + " [id]")
    public void execute(final @NotNull CommandSource source, final @Argument("id") UUID uuid) {
        @Nullable
        final ClickCallbackRegistry.CallbackRegistration registration = ClickCallbackRegistry.INSTANCE
                .query(uuid)
                .orElse(null);

        if(registration == null) {
            ImpactorTranslations.INVALID_CLICK_CALLBACK.send(source, Context.empty());
            return;
        }

        boolean expired = false;
        boolean allow = true;

        // Check use count
        final int allowed = registration.options().uses();
        if(allowed != ClickCallback.UNLIMITED_USES) {
            final int useCount = registration.useCount().incrementAndGet();
            if(useCount >= allowed) {
                expired = true;
                allow = !(useCount > allowed);
            }
        }

        // Check duration expiry
        final Instant now = Instant.now();
        if(now.isAfter(registration.expiration())) {
            expired = true;
            allow = false;
        }

        if(expired) {
            ClickCallbackRegistry.INSTANCE.invalidate(uuid);
        }

        if(allow) {
            registration.callback().accept(source);
        } else {
            ImpactorTranslations.INVALID_CLICK_CALLBACK.send(source, Context.empty());
        }
    }

}
