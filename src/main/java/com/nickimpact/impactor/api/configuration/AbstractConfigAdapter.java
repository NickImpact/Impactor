package com.nickimpact.impactor.api.configuration;

import com.google.common.base.Splitter;
import com.google.common.reflect.TypeToken;
import com.nickimpact.impactor.api.plugins.ConfigurableSpongePlugin;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class AbstractConfigAdapter implements ConfigAdapter {

	private final ConfigurableSpongePlugin plugin;

	private ConfigurationNode root;

	private ConfigurationLoader<CommentedConfigurationNode> loader;

	@Override
	public SpongePlugin getPlugin() {
		return this.plugin;
	}

	@Override
	public void init(String resource) {
		try {
			this.loader = HoconConfigurationLoader.builder()
					.setPath(makeFile(resource))
					.build();

			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ConfigurationNode resolvePath(String path) {
		Iterable<String> paths = Splitter.on('.').split(path);
		ConfigurationNode node = root;

		if (node == null) {
			throw new RuntimeException("Config is not loaded.");
		}

		for (String s : paths) {
			node = node.getNode(s);

			if (node == null) {
				return SimpleConfigurationNode.root();
			}
		}

		return node;
	}

	private Path makeFile(String resource) throws IOException {
		File cfg = plugin.getConfigDir().resolve(resource).toFile();
		//noinspection ResultOfMethodCallIgnored
		cfg.getParentFile().mkdirs();

		if (!cfg.exists()) {
			try (InputStream is = plugin.getClass().getClassLoader().getResourceAsStream(resource)) {
				Files.copy(is, cfg.toPath());
			}
		}

		return cfg.toPath();
	}

	private <T> T getValue(String path, T def) {
		if(this.contains(path)) {
			return (T) resolvePath(path).getValue();
		} else {
			plugin.getConsole().ifPresent(console -> console.sendMessages(
					Text.of(plugin.getPluginInfo().warning(), String.format("Found missing config option for plugin \"%s\"...", plugin.getPluginInfo().getName())),
					Text.of(plugin.getPluginInfo().warning(), String.format("  Permission: %s", path)),
					Text.of(plugin.getPluginInfo().warning(), "  Adding it now...")
			));
			resolvePath(path).setValue(def);
			try {
				this.loader.save(root);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return def;
		}
	}

	@Override
	public boolean contains(String path) {
		return !resolvePath(path).isVirtual();
	}

	@Override
	public String getString(String path, String def) {
		return getValue(path, def);
	}

	@Override
	public int getInt(String path, int def) {
		return getValue(path, def);
	}

	@Override
	public double getDouble(String path, double def) {
		return getValue(path, def);
	}

	@Override
	public long getLong(String path, long def) {
		return getValue(path, def);
	}

	@Override
	public boolean getBoolean(String path, boolean def) {
		return getValue(path, def);
	}

	@Override
	public List<String> getList(String path, List<String> def) {
		ConfigurationNode node = resolvePath(path);
		if (node.isVirtual()) {
			try {
				resolvePath(path).setValue(new TypeToken<List<String>>() {}, def);
			} catch (ObjectMappingException e) {
				e.printStackTrace();
			}
			return def;
		}

		return node.getList(Object::toString);
	}

	@Override
	public List<String> getObjectList(String path, List<String> def) {
		ConfigurationNode node = resolvePath(path);
		if (node.isVirtual()) {
			try {
				resolvePath(path).setValue(new TypeToken<List<String>>() {}, def);
			} catch (ObjectMappingException e) {
				e.printStackTrace();
			}
			return def;
		}

		return node.getChildrenMap().keySet().stream().map(Object::toString).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getMap(String path, Map<String, String> def) {
		ConfigurationNode node = resolvePath(path);
		if (node.isVirtual()) {
			try {
				resolvePath(path).setValue(new TypeToken<Map<String, String>>() {}, def);
			} catch (ObjectMappingException e) {
				e.printStackTrace();
			}
			return def;
		}

		Map<String, Object> m = (Map<String, Object>) node.getValue(Collections.emptyMap());
		return m.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().toString()));
	}
}
