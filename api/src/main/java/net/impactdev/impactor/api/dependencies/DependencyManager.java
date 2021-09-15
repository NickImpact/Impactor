/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.impactdev.impactor.api.dependencies;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.storage.StorageType;
import net.impactdev.impactor.api.dependencies.classloader.IsolatedClassLoader;
import net.impactdev.impactor.api.dependencies.classloader.PluginClassLoader;
import net.impactdev.impactor.api.dependencies.relocation.Relocation;
import net.impactdev.impactor.api.dependencies.relocation.RelocationHandler;
import net.impactdev.impactor.api.utilities.PrettyPrinter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Responsible for loading runtime dependencies.
 */
public class DependencyManager {

	/** The plugin instance */
	private final ImpactorPlugin plugin;
	/** A registry containing plugin specific behavior for dependencies */
	private final DependencyRegistry registry;
	/** THe path where library jars are cached */
	private final Path cacheDirectory;

	/** A map of dependencies which have already been loaded */
	private final EnumMap<Dependency, Path> loaded = new EnumMap<>(Dependency.class);
	/** A map of isolated classloaders which have been created */
	private final Map<ImmutableSet<Dependency>, IsolatedClassLoader> loaders = new HashMap<>();
	/** Cached relocation handler instance */
	private RelocationHandler relocationHandler = null;

	public DependencyManager(ImpactorPlugin plugin) {
		this.plugin = plugin;
		this.registry = new DependencyRegistry();
		this.cacheDirectory = new File("impactor").toPath().resolve("libs");
		if(Files.notExists(this.cacheDirectory)) {
			try {
				Files.createDirectories(this.cacheDirectory);
			} catch (IOException e) {
				throw new RuntimeException("Failed to create libs directory", e);
			}
		}
	}

	private synchronized RelocationHandler getRelocationHandler() {
		if (this.relocationHandler == null) {
			this.relocationHandler = new RelocationHandler(this);
		}
		return this.relocationHandler;
	}

	public DependencyRegistry getRegistry() {
		return this.registry;
	}

	public IsolatedClassLoader obtainClassLoaderWith(Collection<Dependency> dependencies) {
		ImmutableSet<Dependency> set = ImmutableSet.copyOf(dependencies);

		for (Dependency dependency : dependencies) {
			if (!this.loaded.containsKey(dependency)) {
				throw new IllegalStateException("Dependency " + dependency + " is not loaded.");
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
		final ExecutorService executor = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors(),
				new ThreadFactoryBuilder()
						.setNameFormat("Impactor Dependency Downloader - #%d")
						.setDaemon(true)
						.build()
		);

		PrettyPrinter printer = new PrettyPrinter();
		CountDownLatch latch = new CountDownLatch(dependencies.size());
		for(Dependency dependency : dependencies) {
			executor.execute(() -> {
				try {
					this.loadDependency(dependency);
				} catch (Throwable e) {
					this.plugin.getPluginLogger().error("Unable to load dependency " + dependency.name());
					e.printStackTrace();
				} finally {
					latch.countDown();
				}
			});
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			executor.shutdown();
		}
	}

	private void loadDependency(Dependency dependency) throws Exception {
		if(this.loaded.containsKey(dependency)) {
			return;
		}

		Path file = remapDependency(dependency, downloadDependency(dependency));

		this.loaded.put(dependency, file);
		if(this.registry.shouldAutoLoad(dependency)) {
			Impactor.getInstance().getRegistry().get(PluginClassLoader.class).addJarToClasspath(file);
		}
	}

	private Path downloadDependency(Dependency dependency) throws DependencyDownloadException {
		Path file = this.cacheDirectory.resolve(dependency.getFileName() + ".jar");

		if(Files.exists(file)) {
			return file;
		}

		DependencyDownloadException last = null;

		for(DependencyRepository repository : DependencyRepository.values()) {
			try {
				repository.download(dependency, file);
				return file;
			} catch (DependencyDownloadException e) {
				last = e;
			}
		}

		throw Objects.requireNonNull(last);
	}

	private Path remapDependency(Dependency dependency, Path normalFile) throws Exception {
		List<Relocation> rules = Lists.newArrayList(dependency.getRelocations());

		if(rules.isEmpty()) {
			return normalFile;
		}

		Path remapped = this.cacheDirectory.resolve(dependency.getFileName() + "-remapped.jar");

		if(Files.exists(remapped)) {
			return remapped;
		}

		this.plugin.getPluginLogger().info("Applying relocations to " + normalFile.getFileName().toString() + "...");
		getRelocationHandler().remap(normalFile, remapped, rules);
		return remapped;
	}

}