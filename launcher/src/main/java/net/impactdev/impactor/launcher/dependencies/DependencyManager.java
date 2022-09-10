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

package net.impactdev.impactor.launcher.dependencies;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.launcher.dependencies.relocations.Relocation;
import net.impactdev.impactor.launcher.dependencies.relocations.RelocationHandler;
import net.impactdev.impactor.launcher.dependencies.repositories.DependencyRepository;
import net.impactdev.impactor.launcher.dependencies.repositories.ProvidedRepositories;
import net.impactdev.impactor.launcher.dependencies.runtime.appenders.ClassPathAppender;
import net.impactdev.impactor.launcher.dependencies.runtime.loaders.IsolatedClassLoader;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.storage.StorageType;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.launcher.loader.JarInJarClassLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * Responsible for loading runtime dependencies.
 */
public final class DependencyManager {

    /** The plugin instance */
    private final PluginLogger logger;
    private final ClassPathAppender appender;

    /** A registry containing plugin specific behavior for dependencies */
    private final DependencyRegistry registry;
    /** The path where library jars are cached */
    private final Path cacheDirectory;

    /** A set of repositories the manager will query for dependencies */
    private final Set<DependencyRepository> repositories = new TreeSet<>(new DependencyRepository.DependencyComparator());

    private final DependencyDownloader downloader;

    /** A map of dependencies which have already been loaded */
    private final Map<Dependency, Path> loaded = new HashMap<>();
    /** A map of isolated classloaders which have been created */
    private final Map<ImmutableSet<Dependency>, IsolatedClassLoader> loaders = new HashMap<>();
    /** Cached relocation handler instance */
    private RelocationHandler relocationHandler = null;

    public DependencyManager(PluginLogger logger, ClassPathAppender appender) {
        this.logger = logger;
        this.appender = appender;

        this.registry = new DependencyRegistry();
        this.downloader = new DependencyDownloader(logger);
        this.cacheDirectory = new File("impactor").toPath().resolve("libs");
        if(Files.notExists(this.cacheDirectory)) {
            try {
                Files.createDirectories(this.cacheDirectory);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create libs directory", e);
            }
        }

        for(ProvidedRepositories repository : ProvidedRepositories.values()) {
            this.repository(repository);
        }
    }

    private synchronized RelocationHandler getRelocationHandler() {
        if (this.relocationHandler == null) {
            this.relocationHandler = new RelocationHandler(this);
        }
        return this.relocationHandler;
    }

    public Set<DependencyRepository> repositories() {
        return this.repositories;
    }

    public void repository(DependencyRepository repository) {
        this.repositories.add(repository);
    }

    public DependencyRegistry registry() {
        return this.registry;
    }

    public IsolatedClassLoader obtainClassLoaderWith(Dependency dependency) {
        return this.obtainClassLoaderWith(this.flattenBundle(dependency), true);
    }

    public IsolatedClassLoader obtainClassLoaderWith(Collection<Dependency> dependencies) {
        return this.obtainClassLoaderWith(dependencies, false);
    }

    private IsolatedClassLoader obtainClassLoaderWith(Collection<Dependency> dependencies, boolean flattened) {
        ImmutableSet<Dependency> set;
        if(flattened) {
            set = ImmutableSet.copyOf(dependencies);
        } else {
            Set<Dependency> results = new HashSet<>();
            for(Dependency dependency : dependencies) {
                this.flattenBundle$recursive(dependency, results);
            }

            set = ImmutableSet.copyOf(results);
        }

        for (Dependency dependency : dependencies) {
            if (!this.loaded.containsKey(dependency)) {
                throw new IllegalStateException("Dependency " + dependency.name() + " is not loaded.");
            }
        }

        synchronized (this.loaders) {
            IsolatedClassLoader classLoader = this.loaders.get(set);
            if (classLoader != null) {
                return classLoader;
            }

            URL[] urls = set.stream()
                    .map(this.loaded::get)
                    .map(file -> {
                        try {
                            return file.toUri().toURL();
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toArray(URL[]::new);

            classLoader = new IsolatedClassLoader(urls);
            this.loaders.put(set, classLoader);
            return classLoader;
        }
    }

    public void loadStorageDependencies(Collection<StorageType> storageTypes) {
        loadDependencies(this.registry.resolveStorageDependencies(storageTypes));
    }

    public void loadDependencies(Collection<Dependency> dependencies) {
        for(Dependency dependency : dependencies) {
            try {
                this.loadDependency(dependency);
            } catch (Throwable e) {
                this.logger.error("Unable to load dependency " + dependency.name());
                e.printStackTrace();
            }
        }
    }

    public void loadDependencies(Dependency... dependencies) {
        this.loadDependencies(Arrays.asList(dependencies));
    }

    private void loadDependency(Dependency dependency) throws Exception {
        if(this.loaded.containsKey(dependency)) {
            return;
        }

        this.logger.info("Loading dependency: " + dependency.name());
        Path file = remapDependency(dependency, downloadDependency(dependency));
        this.load(dependency, file);

        for(Dependency bundled : dependency.bundled()) {
            this.loadDependency(bundled);
        }
    }

    private void load(Dependency dependency, Path file) {
        this.loaded.put(dependency, file);
        if(this.registry.shouldAutoLoad(dependency)) {
            appender.addJarToClasspath(file);
        }
    }

    private Path downloadDependency(Dependency dependency) throws DependencyDownloadException {
        Path file = this.cacheDirectory.resolve(dependency.getFileName() + ".jar");

        if(Files.exists(file)) {
            return file;
        }

        List<DependencyDownloadException> exceptions = Lists.newArrayList();
        for(DependencyRepository repository : this.repositories) {
            try {
                this.downloader.download(repository, dependency, file);
                return file;
            } catch (DependencyDownloadException e) {
                exceptions.add(e);
            }
        }

        new PrettyPrinter(80)
                .title("Noted Exceptions per Repository")
                .consume(printer -> {
                    for(DependencyDownloadException exception : exceptions) {
                        printer.add(exception).newline();
                    }
                })
                .log(this.logger, PrettyPrinter.Level.ERROR);
        throw new RuntimeException();
    }

    private Path remapDependency(Dependency dependency, Path normalFile) throws Exception {
        List<Relocation> rules = Lists.newArrayList(dependency.relocations());

        if(rules.isEmpty()) {
            return normalFile;
        }

        Path remapped = this.cacheDirectory.resolve(dependency.getFileName() + "-remapped.jar");
        if(Files.exists(remapped)) {
            return remapped;
        }

        this.getRelocationHandler().remap(normalFile, remapped, rules);
        return remapped;
    }

    private Set<Dependency> flattenBundle(Dependency dependency) {
        return this.flattenBundle$recursive(dependency, new HashSet<>());
    }

    private Set<Dependency> flattenBundle$recursive(Dependency dependency, Set<Dependency> set) {
        set.add(dependency);
        for(Dependency bundled : dependency.bundled()) {
            this.flattenBundle$recursive(bundled, set);
        }

        return set;
    }

    public static class DependencyDownloader {

        private final PluginLogger logger;

        public DependencyDownloader(PluginLogger logger) {
            this.logger = logger;
        }

        public void download(DependencyRepository repository, Dependency dependency, Path file) throws DependencyDownloadException {
            try {
                Files.write(file, download(repository, dependency));
            } catch (IOException e) {
                throw new DependencyDownloadException(e);
            }
        }

        private URLConnection openConnection(DependencyRepository repository, Dependency dependency) throws IOException {
            URLConnection connection;
            if(dependency.snapshot() && repository.snapshots().isPresent()) {
                URL url = repository.snapshots().get().resolve(dependency);
                connection = url.openConnection();
            } else {
                URL dependencyURL = new URL(repository.releases() + dependency.getMavenPath());
                connection = dependencyURL.openConnection();
            }

            connection.setRequestProperty("User-Agent", "impactor");
            connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(5));
            connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10));
            return connection;
        }

        private byte[] downloadRaw(DependencyRepository repository, Dependency dependency) throws DependencyDownloadException {
            try {
                URLConnection connection = this.openConnection(repository, dependency);
                try(InputStream in = connection.getInputStream()) {
                    byte[] bytes = ByteStreams.toByteArray(in);
                    if(bytes.length == 0) {
                        throw new Exception("empty stream");
                    }

                    return bytes;
                }
            } catch (Exception e) {
                throw new DependencyDownloadException(e);
            }
        }

        private byte[] download(DependencyRepository repository, Dependency dependency) throws DependencyDownloadException {
            byte[] bytes = downloadRaw(repository, dependency);
            Optional<byte[]> checksum = dependency.checksum();
            try {
                byte[] hash = MessageDigest.getInstance("SHA-256").digest(bytes);
                this.logger.debug("Checksum = " + Base64.getEncoder().encodeToString(hash));

                if(checksum.isPresent()) {
                    if (!Arrays.equals(checksum.get(), hash)) {
                        throw new DependencyDownloadException("Mismatched checksum for " + dependency.name() +
                                ". Expected: " + Base64.getEncoder().encodeToString(checksum.get()) + " " +
                                "Actual: " + Base64.getEncoder().encodeToString(hash));
                    }
                }
            } catch (NoSuchAlgorithmException e) {
                throw new DependencyDownloadException("Failed to decode file hash", e);
            }

            this.logger.info("Successfully downloaded dependency '" + dependency.name() + "'");
            return bytes;
        }

    }


}