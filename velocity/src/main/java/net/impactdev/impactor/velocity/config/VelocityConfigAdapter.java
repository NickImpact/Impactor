package net.impactdev.impactor.velocity.config;

import com.google.common.base.Splitter;
import net.impactdev.impactor.api.configuration.ConfigurationAdapter;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VelocityConfigAdapter implements ConfigurationAdapter {

    private final ImpactorPlugin plugin;
    private final Path path;
    private ConfigurationNode root;

    public VelocityConfigAdapter(ImpactorPlugin plugin, File path) {
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
        return HoconConfigurationLoader.builder().setPath(path).build();
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

        return this.root.getNode(Splitter.on('.').splitToList(path).toArray());
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
        if (node.isVirtual()) {
            return def;
        }

        return node.getList(Object::toString);
    }

    @Override
    public List<String> getKeys(String path, List<String> def) {
        ConfigurationNode node = resolvePath(path);
        if (node.isVirtual()) {
            return def;
        }

        return node.getChildrenMap().keySet().stream().map(Object::toString).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getStringMap(String path, Map<String, String> def) {
        ConfigurationNode node = resolvePath(path);
        if (node.isVirtual()) {
            return def;
        }

        Map<String, Object> m = (Map<String, Object>) node.getValue(Collections.emptyMap());
        return m.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().toString()));
    }
}
