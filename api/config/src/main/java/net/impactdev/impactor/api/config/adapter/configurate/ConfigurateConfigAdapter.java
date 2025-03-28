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

package net.impactdev.impactor.api.config.adapter.configurate;

import com.google.common.base.Splitter;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.config.adapter.ConfigurationAdapter;
import net.impactdev.impactor.api.core.annotations.DesignBasedOn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Represents a configuration adapter based around configurate.
 *
 * @since 6.0.0
 */
@DesignBasedOn(
        github = "https://github.com/LuckPerms/LuckPerms/tree/master",
        license = "https://github.com/LuckPerms/LuckPerms/blob/master/LICENSE.txt"
)
public abstract class ConfigurateConfigAdapter implements ConfigurationAdapter {

    private final Path path;
    private ConfigurationNode root;

    /**
     * Creates a new {@link ConfigurateConfigAdapter}, using the given path as the file source.
     *
     * @param path The target location of the config file
     * @since 6.0.0
     */
    public ConfigurateConfigAdapter(Path path) {
        this(path, null);
    }

    /**
     * Creates a new {@link ConfigurateConfigAdapter}, using the given path as the file source. Should the
     * target config file not be present, it will try to be created using a given supplier leading to an
     * {@link InputStream}, if defined.
     *
     * @param path     The target location of the config file
     * @param supplier An optional supplier used to create the config file if not already created
     * @since 6.0.0
     */
    public ConfigurateConfigAdapter(final @NotNull Path path, final @Nullable Supplier<InputStream> supplier) {
        this.path = path;
        this.reload();
    }

    protected abstract ConfigurationLoader<? extends ConfigurationNode> createLoader(Path path);

    private void createConfigIfNecessary(@Nullable Supplier<InputStream> supplier) {
        if (!Files.exists(this.path)) {
            try {
                this.createDirectoriesIfNotExists(this.path.getParent());

                if (supplier != null) {
                    Files.copy(supplier.get(), this.path);
                } else {
                    Files.createFile(this.path);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void createDirectoriesIfNotExists(Path path) throws IOException {
        if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
            return;
        }

        Files.createDirectories(path);
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

    private ConfigurationNode resolve(String path) {
        if (this.root == null) {
            throw new RuntimeException("Configuration is not loaded");
        }

        return this.root.node(Splitter.on('.').splitToList(path));
    }

    @Override
    public String getString(String path, String def) {
        return this.resolve(path).getString(def);
    }

    @Override
    public byte getByte(String path, byte def) {
        return (byte) this.getInteger(path, def);
    }

    @Override
    public short getShort(String path, short def) {
        return (short) this.getInteger(path, def);
    }

    @Override
    public int getInteger(String path, int def) {
        return this.resolve(path).getInt(def);
    }

    @Override
    public float getFloat(String path, float def) {
        return this.resolve(path).getFloat(def);
    }

    @Override
    public long getLong(String path, long def) {
        return this.resolve(path).getLong(def);
    }

    @Override
    public double getDouble(String path, double def) {
        return this.resolve(path).getDouble(def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return this.resolve(path).getBoolean(def);
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        ConfigurationNode node = resolve(path);
        if (node.virtual() || !node.isList()) {
            return def;
        }

        try {
            return node.getList(String.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> getStringMap(String path, Map<String, String> def) {
        ConfigurationNode node = resolve(path);
        if (node.virtual()) {
            return def;
        }

        try {
            Map<String, String> mappings = Optional.ofNullable(node.get(new TypeToken<Map<String, String>>() {
                    }))
                    .orElse(Collections.emptyMap());

            return mappings.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getKeys(String path, List<String> def) {
        ConfigurationNode node = resolve(path);
        if (node.virtual()) {
            return def;
        }

        return node.childrenMap().keySet().stream().map(Object::toString).collect(Collectors.toList());
    }
}
