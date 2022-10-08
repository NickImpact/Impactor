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

package net.impactdev.impactor.configuration;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.configuration.ConfigPath;
import net.impactdev.impactor.api.configuration.ConfigurationAdapter;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ImpactorConfigurationAdapter implements ConfigurationAdapter {

    private final Path path;
    private ConfigurationNode root;
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    /** Whether the config should update and insert new keys as they are added */
    private final boolean update;

    public ImpactorConfigurationAdapter(Path path, boolean update) {
        this.path = path;
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

    private <T> void checkMissing(ConfigPath path, T def) {
        if (update && !this.contains(path)) {
            try {
                resolvePath(path).set(def);
                this.loader.save(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private <T> void checkMissing(ConfigPath path, T def, TypeToken<T> type) {
        if (update && !this.contains(path)) {
            try {
                resolvePath(path).set(type.getType(), def);
                this.loader.save(root);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean contains(ConfigPath path) {
        return !resolvePath(path).virtual();
    }

    private ConfigurationNode resolvePath(ConfigPath path) {
        if (this.root == null) {
            throw new RuntimeException("Config is not loaded.");
        }

        if(path.split()) {
            return this.root.node(Splitter.on('.').splitToList(path.target()).toArray());
        } else {
            return this.root.node(path.target());
        }
    }

    @Override
    public String getString(ConfigPath path, String def) {
        this.checkMissing(path, def);
        return resolvePath(path).getString(def);
    }

    @Override
    public int getInteger(ConfigPath path, int def) {
        this.checkMissing(path, def);
        return resolvePath(path).getInt(def);
    }

    @Override
    public long getLong(ConfigPath path, long def) {
        this.checkMissing(path, def);
        return resolvePath(path).getLong(def);
    }

    @Override
    public double getDouble(ConfigPath path, double def) {
        this.checkMissing(path, def);
        return resolvePath(path).getDouble(def);
    }

    @Override
    public boolean getBoolean(ConfigPath path, boolean def) {
        this.checkMissing(path, def);
        return resolvePath(path).getBoolean(def);
    }

    @Override
    public List<String> getStringList(ConfigPath path, List<String> def) {
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
    public List<String> getKeys(ConfigPath path, List<String> def) {
        this.checkMissing(path, def, new TypeToken<List<String>>() {});
        ConfigurationNode node = resolvePath(path);
        if (node.virtual()) {
            return def;
        }

        return node.childrenMap().keySet().stream().map(Object::toString).collect(Collectors.toList());
    }

    @Override
    public Map<String, String> getStringMap(ConfigPath path, Map<String, String> def) {
        this.checkMissing(path, def, new TypeToken<Map<String, String>>() {});
        ConfigurationNode node = resolvePath(path);
        if (node.virtual()) {
            return def;
        }

        Map<String, String> m;
        try {
            m = Optional.ofNullable(node.get(new io.leangen.geantyref.TypeToken<Map<String, String>>() {})).orElse(
                    Collections.emptyMap());
        } catch (SerializationException e) {
            e.printStackTrace();
            m = Collections.emptyMap();
        }
        return m.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
