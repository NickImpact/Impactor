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

package net.impactdev.impactor.core.translations;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.api.translations.TranslationManager;
import net.impactdev.impactor.api.translations.repository.TranslationRepository;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.translations.builders.ImpactorTranslationManagerBuilder;
import net.impactdev.impactor.core.translations.components.TranslationSet;
import net.impactdev.impactor.core.translations.repository.ImpactorTranslationRepository;
import net.kyori.adventure.translation.Translator;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImpactorTranslationManager implements TranslationManager {

    private final TranslationRepository repository;
    private final Locale defaultLocale;
    private final Path root;
    private final TextProcessor processor;
    private final Supplier<InputStream> supplier;

    private final Cache<Locale, TranslationSet> translations = Caffeine.newBuilder().build();
    private final Set<Locale> installed = ConcurrentHashMap.newKeySet();

    public ImpactorTranslationManager(ImpactorTranslationManagerBuilder builder) {
        this.repository = builder.repository;
        this.defaultLocale = builder.defaultLocale;
        this.root = builder.path;
        this.processor = builder.processor;
        this.supplier = builder.supplier;

        ((ImpactorTranslationRepository) this.repository).manager(this);
    }

    @Override
    public TranslationRepository repository() {
        return this.repository;
    }

    @Override
    public void reload() {
        this.translations.invalidateAll();
        this.installed.clear();

        // Load translations, such that if a language is registered, it is final
        this.loadFromFileSystem(this.root.resolve("custom"));
        this.loadFromFileSystem(this.root.resolve("repository"));

        if(this.supplier != null) {
            Gson simple = new GsonBuilder().create();
            JsonObject json = simple.fromJson(new InputStreamReader(this.supplier.get()), JsonObject.class);

            try {
                this.translations.put(this.defaultLocale, TranslationSet.fromJson(json));
                this.installed.add(this.defaultLocale);
            } catch (Exception e) {
                ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
            }
        }
    }

    @Override
    public void refresh() {
        this.repository.refresh().whenComplete((result, tracked) -> {
            if(tracked != null) {
                ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), tracked);
            }

            if(result) {
                this.reload();
            }
        });
    }

    @Override
    public TextProcessor processor() {
        return this.processor;
    }

    @Override
    public Locale defaultLocale() {
        return this.defaultLocale;
    }

    @Override
    public Set<Locale> installed() {
        return Collections.unmodifiableSet(this.installed);
    }

    @Override
    public Path root() {
        return this.root;
    }

    public TranslationSet fetch(Locale locale) {
        return this.translations.get(
                locale,
                key -> Optional.ofNullable(this.translations.getIfPresent(this.defaultLocale()))
                        .orElseThrow(() -> new IllegalStateException("Fallback locale had no registration..."))
        );
    }

    public void initialize() {
        try {
            Path repository = this.root.resolve("repository");
            if(Files.notExists(repository)) {
                Files.createDirectories(repository);
            }

            Path custom = this.root.resolve("custom");
            if(Files.notExists(custom)) {
                Files.createDirectories(custom);
            }

            this.reload();
        } catch (Exception exception) {
            ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), exception);
        }
    }

    private void loadFromFileSystem(Path directory) {
        try(Stream<Path> stream = Files.list(directory)) {
            List<Path> translations = stream.filter(ImpactorTranslationManager::isConfigurationFile).collect(Collectors.toList());
            for(Path translation : translations) {
                try {
                    Map.Entry<Locale, TranslationSet> result = this.loadTranslationsFromFile(translation);
                    if(this.installed.add(result.getKey())) {
                        this.translations.put(result.getKey(), result.getValue());
                    }
                } catch (Exception e) {
                    BaseImpactorPlugin.instance().logger().warn("Error loading locale file: " + translation.getFileName());
                }
            }
        } catch (Exception e) {
            BaseImpactorPlugin.instance().logger().severe("Exception occurred loading translations...");
            ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
        }

        this.translations.asMap().forEach((locale, config) -> {
            Locale noCountry = new Locale(locale.getLanguage());
            if(!locale.equals(noCountry) && this.installed.add(noCountry)) {
                this.translations.put(locale, config);
            }
        });
    }

    private Map.Entry<Locale, TranslationSet> loadTranslationsFromFile(Path target) throws Exception {
        String name = target.getFileName().toString();
        String localeString = name.substring(0, name.length() - ".conf".length());
        Locale locale = TranslationManager.parseLocale(localeString);
        if(locale == null) {
            throw new IllegalStateException("Unknown locale '" + localeString + "' - skipping registration");
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject json = gson.fromJson(new FileReader(target.toFile()), JsonObject.class);

        return Maps.immutableEntry(locale, TranslationSet.fromJson(json));
    }

    public static boolean isConfigurationFile(Path path) {
        return path.getFileName().toString().endsWith(".conf");
    }

}
