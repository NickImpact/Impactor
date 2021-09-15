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

package net.impactdev.impactor.bungee.configuration;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.configuration.ConfigurationAdapter;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BungeeConfigAdapter implements ConfigurationAdapter {

	private final ImpactorPlugin plugin;
	private final Path path;
	private ConfigurationNode root;

	public BungeeConfigAdapter(ImpactorPlugin plugin, File path) {
		this.plugin = plugin;
		this.path = path.toPath();
		this.createConfigIfMissing();
		reload();
	}

	private void createConfigIfMissing() {
		if(!Files.exists(this.path)) {
			try {
				this.createDirectoriesIfNotExists(this.path.getParent());
				Files.createFile(this.path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void createDirectoriesIfNotExists(Path path) throws IOException {
		if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
			return;
		}

		Files.createDirectories(path);
	}

	private ConfigurationLoader<? extends ConfigurationNode> createLoader(Path path) {
		return HoconConfigurationLoader.builder().path(path).build();
	}

	@Override
	public void reload() {
		ConfigurationLoader<? extends ConfigurationNode> loader = createLoader(this.path);
		try {
			this.root = loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private ConfigurationNode resolvePath(String path) {
		if (this.root == null) {
			throw new RuntimeException("Config is not loaded.");
		}

		return this.root.node(Splitter.on('.').splitToList(path).toArray());
	}

	@Override
	public String getString(String path, String def) {
		return resolvePath(path).getString(def);
	}

	@Override
	public int getInteger(String path, int def) {
		return resolvePath(path).getInt(def);
	}

	@Override
	public long getLong(String path, long def) {
		return resolvePath(path).getLong(def);
	}

	@Override
	public double getDouble(String path, double def) {
		return resolvePath(path).getDouble(def);
	}

	@Override
	public boolean getBoolean(String path, boolean def) {
		return resolvePath(path).getBoolean(def);
	}

	@Override
	public List<String> getStringList(String path, List<String> def) {
		ConfigurationNode node = resolvePath(path);
		if (node.virtual()) {
			return def;
		}

		try {
			return node.getList(String.class);
		} catch (SerializationException e) {
			e.printStackTrace();
			return Lists.newArrayList();
		}
	}

	@Override
	public List<String> getKeys(String path, List<String> def) {
		ConfigurationNode node = resolvePath(path);
		if (node.virtual()) {
			return def;
		}

		return node.childrenMap().keySet().stream().map(Object::toString).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getStringMap(String path, Map<String, String> def) {
		ConfigurationNode node = resolvePath(path);
		if (node.virtual()) {
			return def;
		}

		try {
			return node.get(new TypeToken<Map<String, Object>>(){}, Collections.emptyMap())
					.entrySet()
					.stream()
					.collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().toString()));
		} catch (SerializationException e) {
			e.printStackTrace();
			return Collections.emptyMap();
		}
	}
}
