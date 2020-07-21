package com.nickimpact.impactor.spigot.configuration;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.configuration.ConfigurationAdapter;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SpigotConfigAdapter implements ConfigurationAdapter {

	private ImpactorPlugin plugin;
	private final File file;
	private YamlConfiguration configuration;

	public SpigotConfigAdapter(ImpactorPlugin plugin, File file) {
		this.plugin = plugin;
		this.file = file;
		this.createIfNotExists();
		this.reload();
	}

	private void createIfNotExists() {
		if(!this.file.exists()) {
			this.file.getParentFile().mkdirs();
			try {
				this.file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void checkMissing(String path, Object def) {
		if(!this.contains(path)) {
			plugin.getPluginLogger().warn(Lists.newArrayList(String.format("Found missing config option (%s)... Adding it!", path)));

			configuration.set(path, def);
			try {
				this.configuration.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean contains(String path) {
		return this.configuration.contains(path);
	}

	public <T> void set(String path, T def) {
		configuration.set(path, def);
		try {
			this.configuration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reload() {
		this.configuration = YamlConfiguration.loadConfiguration(this.file);
	}

	@Override
	public String getString(String path, String def) {
		this.checkMissing(path, def);
		return this.configuration.getString(path, def);
	}

	@Override
	public int getInteger(String path, int def) {
		this.checkMissing(path, def);
		return this.configuration.getInt(path, def);
	}

	@Override
	public long getLong(String path, long def) {
		this.checkMissing(path, def);
		return this.configuration.getLong(path, def);
	}

	@Override
	public double getDouble(String path, double def) {
		this.checkMissing(path, def);
		return this.configuration.getDouble(path, def);
	}

	@Override
	public boolean getBoolean(String path, boolean def) {
		this.checkMissing(path, def);
		return this.configuration.getBoolean(path, def);
	}

	@Override
	public List<String> getStringList(String path, List<String> def) {
		this.checkMissing(path, def);
		List<String> ret = this.configuration.getStringList(path);
		return ret == null ? def : ret;
	}

	@Override
	public List<String> getKeys(String path, List<String> def) {
		this.checkMissing(path, def);
		ConfigurationSection section = this.configuration.getConfigurationSection(path);
		if (section == null) {
			return def;
		}

		Set<String> keys = section.getKeys(false);
		return keys == null ? def : new ArrayList<>(keys);
	}

	@Override
	public Map<String, String> getStringMap(String path, Map<String, String> def) {
		this.checkMissing(path, def);
		Map<String, String> map = new HashMap<>();
		ConfigurationSection section = this.configuration.getConfigurationSection(path);
		if (section == null) {
			return def;
		}

		for (String key : section.getKeys(false)) {
			map.put(key, section.getString(key));
		}

		return map;
	}
}
