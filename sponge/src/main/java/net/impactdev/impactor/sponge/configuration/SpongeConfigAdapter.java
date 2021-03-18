package net.impactdev.impactor.sponge.configuration;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigRenderOptions;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.configuration.ConfigurationAdapter;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
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
import java.util.Optional;
import java.util.stream.Collectors;

public class SpongeConfigAdapter implements ConfigurationAdapter {

	private final ImpactorPlugin plugin;
	private final Path path;
	private ConfigurationNode root;
	private ConfigurationLoader<CommentedConfigurationNode> loader;

	/** Whether or not the config should update and insert new keys as they are added */
	private final boolean update;

	public SpongeConfigAdapter(ImpactorPlugin plugin, File path) {
		this(plugin, path, false);
	}

	public SpongeConfigAdapter(ImpactorPlugin plugin, File path, boolean update) {
		this.plugin = plugin;
		this.path = path.toPath();
		this.update = update;
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
		return (this.loader = HoconConfigurationLoader.builder().path(path).build());
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

	private <T> void checkMissing(String path, T def) {
		if (update && !this.contains(path)) {
			try {
				resolvePath(path).set(def);
				this.loader.save(root);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private <T> void checkMissing(String path, T def, TypeToken<T> type) {
		if (update && !this.contains(path)) {
			try {
				resolvePath(path).set(type.getType(), def);
				this.loader.save(root);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean contains(String path) {
		return !resolvePath(path).virtual();
	}

	private ConfigurationNode resolvePath(String path) {
		if (this.root == null) {
			throw new RuntimeException("Config is not loaded.");
		}

		return this.root.node(Splitter.on('.').splitToList(path).toArray());
	}

	@Override
	public String getString(String path, String def) {
		this.checkMissing(path, def);
		return resolvePath(path).getString(def);
	}

	@Override
	public int getInteger(String path, int def) {
		this.checkMissing(path, def);
		return resolvePath(path).getInt(def);
	}

	@Override
	public long getLong(String path, long def) {
		this.checkMissing(path, def);
		return resolvePath(path).getLong(def);
	}

	@Override
	public double getDouble(String path, double def) {
		this.checkMissing(path, def);
		return resolvePath(path).getDouble(def);
	}

	@Override
	public boolean getBoolean(String path, boolean def) {
		this.checkMissing(path, def);
		return resolvePath(path).getBoolean(def);
	}

	@Override
	public List<String> getStringList(String path, List<String> def) {
		this.checkMissing(path, def, new TypeToken<List<String>>() {});
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
		this.checkMissing(path, def, new TypeToken<List<String>>() {});
		ConfigurationNode node = resolvePath(path);
		if (node.virtual()) {
			return def;
		}

		return node.childrenMap().keySet().stream().map(Object::toString).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getStringMap(String path, Map<String, String> def) {
		this.checkMissing(path, def, new TypeToken<Map<String, String>>() {});
		ConfigurationNode node = resolvePath(path);
		if (node.virtual()) {
			return def;
		}

		Map<String, Object> m;
		try {
			m = Optional.ofNullable(node.get(new io.leangen.geantyref.TypeToken<Map<String, Object>>() {})).orElse(Collections.emptyMap());
		} catch (SerializationException e) {
			e.printStackTrace();
			m = Collections.emptyMap();
		}
		return m.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().toString()));
	}
}
