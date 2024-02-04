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

package net.impactdev.impactor.core.commands.translations;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.translations.metadata.LanguageInfo;
import net.impactdev.impactor.api.utility.Context;
import net.impactdev.impactor.core.translations.internal.ImpactorTranslations;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.processing.CommandContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Set;

@CommandContainer
@Permission("impactor.commands.translations.base")
public final class TranslationCommands {

    @Command("translations view <language>")
    @Permission("impactor.commands.translations.view")
    public void view(final @NotNull CommandSource source, @Argument("language") Locale locale) {
        ImpactorTranslations.TRANSLATIONS_SEARCHING.send(source, Context.empty());

        Context context = Context.empty();
        Set<LanguageInfo> languages = ImpactorTranslations.MANAGER.repository().available().join();
        LanguageInfo target = languages.stream()
                .filter(info -> info.locale().equals(locale))
                .findFirst()
                .orElse(null);

        if(target == null) {
            ImpactorTranslations.TRANSLATIONS_INVALID_LOCALE.send(source, context);
            return;
        }

        context.append(LanguageInfo.class, target);

        ImpactorTranslations.TRANSLATIONS_INFO_HEADER_FOOTER.send(source, context);
        ImpactorTranslations.TRANSLATIONS_INFO_LANGUAGE.send(source, context);
        ImpactorTranslations.TRANSLATIONS_INFO_PROGRESS.send(source, context);
        ImpactorTranslations.TRANSLATIONS_INFO_CONTRIBUTOR_HEADER.send(source, context);
        target.contributors().forEach(contributor -> {
            Context ctx = Context.empty().with(context).append(String.class, contributor);
            ImpactorTranslations.TRANSLATIONS_INFO_CONTRIBUTOR_NAME.send(source, ctx);
        });
        ImpactorTranslations.TRANSLATIONS_INFO_HEADER_FOOTER.send(source, context);
    }

    @Command("translations install")
    @Permission("impactor.commands.translations.install")
    public void install(final @NotNull CommandSource source) {
        ImpactorTranslations.TRANSLATIONS_SEARCHING.send(source, Context.empty());
        Set<LanguageInfo> languages = ImpactorTranslations.MANAGER.repository().available().join();

        Context context = Context.empty();
        context.append(new TypeToken<Set<LanguageInfo>>() {}, languages);
        ImpactorTranslations.TRANSLATIONS_INSTALLING.send(source, context);
        ImpactorTranslations.MANAGER
                .repository()
                .downloadAndInstall(languages, source, true)
                .join();

        ImpactorTranslations.TRANSLATIONS_INSTALL_COMPLETE.send(source, context);
    }

}
