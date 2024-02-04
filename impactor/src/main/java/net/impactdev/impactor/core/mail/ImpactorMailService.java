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

package net.impactdev.impactor.core.mail;

import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.mail.MailMessage;
import net.impactdev.impactor.api.mail.MailService;
import net.impactdev.impactor.api.mail.filters.MailFilter;
import net.impactdev.impactor.api.storage.StorageType;
import net.impactdev.impactor.core.economy.EconomyConfig;
import net.impactdev.impactor.core.mail.storage.MailStorage;
import net.impactdev.impactor.core.mail.storage.MailStorageFactory;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class ImpactorMailService implements MailService {

    private final Config config;
    private final MailStorage storage;

    ImpactorMailService() {
        this.config = Config.builder()
                .path(BaseImpactorPlugin.instance().configurationDirectory().resolve("mail.conf"))
                .provider(EconomyConfig.class)
                .provideIfMissing(() -> BaseImpactorPlugin.instance().resource(root -> root.resolve("configs").resolve("mail.conf")))
                .build();

        this.storage = MailStorageFactory.instance(
                BaseImpactorPlugin.instance(),
                this.config.get(MailConfig.STORAGE_TYPE),
                StorageType.JSON
        );
    }

    @Override
    public CompletableFuture<List<MailMessage>> inbox(@NotNull UUID target) {
        return this.storage.mail(target).orTimeout(5, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Boolean> sendFromServer(@NotNull UUID target, @NotNull Component message) {
        return this.storage.send(target, new ImpactorMailMessage(UUID.randomUUID(), null, message, Instant.now()));
    }

    @Override
    public CompletableFuture<Boolean> send(@NotNull UUID source, @NotNull UUID target, @NotNull Component message) {
        return this.storage.send(target, new ImpactorMailMessage(UUID.randomUUID(), source, message, Instant.now()));
    }

    @Override
    public CompletableFuture<TriState> delete(@NotNull UUID target, @NotNull MailMessage message) {
        return this.storage.delete(target, message);
    }

    @Override
    public CompletableFuture<TriState> deleteWhere(@NotNull UUID target, @Nullable MailFilter filter) {
        return this.storage.deleteWhere(target, filter);
    }

    @Override
    public String name() {
        return "Impactor Mail Service";
    }
}
