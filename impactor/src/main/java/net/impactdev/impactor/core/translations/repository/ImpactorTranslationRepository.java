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

package net.impactdev.impactor.core.translations.repository;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.impactdev.impactor.api.translations.TranslationManager;
import net.impactdev.impactor.api.translations.metadata.LanguageInfo;
import net.impactdev.impactor.api.translations.repository.TranslationEndpoint;
import net.impactdev.impactor.api.translations.repository.TranslationRepository;
import net.impactdev.impactor.api.utility.Context;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.translations.ImpactorTranslationManager;
import net.impactdev.impactor.core.translations.builders.ImpactorTranslationRepositoryBuilder;
import net.impactdev.impactor.core.translations.internal.ImpactorTranslations;
import net.impactdev.impactor.core.utility.future.Futures;
import net.kyori.adventure.audience.Audience;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ImpactorTranslationRepository implements TranslationRepository {

    private static final Gson SIMPLE = new GsonBuilder().create();
    private static final Gson PRETTY = new GsonBuilder().setPrettyPrinting().create();

    private final Map<TranslationEndpoint, String> urls = Maps.newHashMap();
    private final long maxBundleSize;
    private final long cacheMaxAge;
    private final Supplier<Boolean> refreshRule;

    private final TranslationsClient client = new TranslationsClient();
    @MonotonicNonNull private TranslationManager manager;

    public ImpactorTranslationRepository(ImpactorTranslationRepositoryBuilder builder) {
        this.urls.putAll(builder.urls);
        this.maxBundleSize = builder.maxBundleSize;
        this.cacheMaxAge = builder.maxCacheAge;
        this.refreshRule = builder.rule;
    }

    @Override
    public CompletableFuture<Set<LanguageInfo>> available() {
        return Futures.execute(() -> this.fetchTranslationsMetadata().languages());
    }

    @Override
    public CompletableFuture<Boolean> refresh() {
        return Futures.execute(() -> {
            long last = this.readLastRefreshTime();
            long since = System.currentTimeMillis() - last;

            if(this.refreshRule != null) {
                if(!this.refreshRule.get()) {
                    return false;
                }
            }

            if(since <= cacheMaxAge) {
                return false;
            }

            MetadataResponse metadata = this.fetchTranslationsMetadata();
            if(since <= metadata.age()) {
                return false;
            }

            this.downloadAndInstall(this.available().join(), Audience.empty(), true).join();
            return true;
        });
    }

    @Override
    public CompletableFuture<Void> downloadAndInstall(@NotNull Set<LanguageInfo> languages, @NotNull Audience audience, boolean update) {
        return Futures.execute(() -> {
            Path target = this.manager.root().resolve("repository");
            this.clear(target, ImpactorTranslationManager::isConfigurationFile);
            try {
                Files.createDirectories(target);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for(LanguageInfo language : languages) {
                Context context = Context.empty();
                context.append(LanguageInfo.class, language);

                ImpactorTranslations.TRANSLATIONS_INSTALLING_LANGUAGE.send(audience, context);

                Request request = new Request.Builder()
                        .header("User-Agent", TranslationsClient.USER_AGENT)
                        .url(String.format(this.urls.get(TranslationEndpoint.DOWNLOADABLE_LANGUAGE), language.id()))
                        .build();

                try (Response response = this.client.makeRequest(request)) {
                    try (ResponseBody body = response.body()) {
                        if(body == null) {
                            throw new IOException("No response from translations server");
                        }

                        Path file = target.resolve(language.locale() + ".conf");
                        try (InputStream stream = new LimitedInputStream(body.byteStream(), maxBundleSize)) {
                            Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                } catch (Exception e) {
                    ImpactorTranslations.TRANSLATIONS_INSTALL_FAILED.send(audience, context);
//                    Translatables.TRANSLATIONS_DOWNLOAD_FAILED.send(audience, Context.empty().append(Locale.class, language.locale()));

                    ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
                }
            }

            if(update) {
                writeLastRefreshTime();
            }

            this.manager.reload();
        });
    }

    public void manager(final @NotNull TranslationManager manager) {
        this.manager = manager;
    }

    private void clear(Path directory, Predicate<Path> filter) {
        try (Stream<Path> paths = Files.list(directory).filter(filter)) {
            paths.forEach(path -> {
                try {
                    Files.delete(path);
                } catch (Exception ignored) {}
            });
        } catch (Exception ignored) {}
    }

    private void writeLastRefreshTime() {
        Path statusFile = this.manager.root().resolve("repository").resolve("status.json");

        try (BufferedWriter writer = Files.newBufferedWriter(statusFile, StandardCharsets.UTF_8)) {
            JsonObject status = new JsonObject();
            status.add("lastRefresh", new JsonPrimitive(System.currentTimeMillis()));
            PRETTY.toJson(status, writer);
        } catch (IOException e) {
            ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
        }
    }

    private long readLastRefreshTime() {
        Path statusFile = this.manager.root().resolve("repository").resolve("status.json");

        if (Files.exists(statusFile)) {
            try (BufferedReader reader = Files.newBufferedReader(statusFile, StandardCharsets.UTF_8)) {
                JsonObject status = SIMPLE.fromJson(reader, JsonObject.class);
                if (status.has("lastRefresh")) {
                    return status.get("lastRefresh").getAsLong();
                }
            } catch (Exception e) {
                ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
            }
        }

        return 0L;
    }

    private MetadataResponse fetchTranslationsMetadata() throws Exception {
        Request request = new Request.Builder()
                .header("User-Agent", TranslationsClient.USER_AGENT)
                .url(this.urls.get(TranslationEndpoint.LANGUAGE_SET))
                .build();

        JsonObject jsonResponse;
        try (Response response = this.client.makeRequest(request)) {
            try (ResponseBody responseBody = response.body()) {
                if (responseBody == null) {
                    throw new RuntimeException("No response");
                }

                try (InputStream inputStream = new LimitedInputStream(responseBody.byteStream(), maxBundleSize)) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                        jsonResponse = SIMPLE.fromJson(reader, JsonObject.class);
                    }
                }
            }
        }

        Set<LanguageInfo> languages = new LinkedHashSet<>();
        for (Map.Entry<String, JsonElement> language : jsonResponse.get("languages").getAsJsonObject().entrySet()) {
            languages.add(new LanguageInfo(language.getKey(), language.getValue().getAsJsonObject()));
        }
        languages.removeIf(language -> language.progress() <= 0);

        if (languages.size() >= 100) {
            // just a precaution: if more than 100 languages have been
            // returned then the metadata server is doing something silly
            throw new IOException("More than 100 languages - cancelling download");
        }

        long cacheMaxAge = jsonResponse.get("timestamp").getAsLong();
        return new MetadataResponse(cacheMaxAge, languages);
    }
}
