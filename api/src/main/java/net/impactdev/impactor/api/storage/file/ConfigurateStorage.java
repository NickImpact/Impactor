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

package net.impactdev.impactor.api.storage.file;

import com.google.common.base.Throwables;
import net.impactdev.impactor.api.plugin.components.Configurable;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.components.Tasking;
import net.impactdev.impactor.api.storage.file.loaders.ConfigurateLoader;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigurateStorage {

    private final ImpactorPlugin plugin;
    private final String implementationName;

    // The loader responsible for I/O
    private final ConfigurateLoader loader;

    private String extension;

    private Path dataDir;
    private String dataDirName;

    private FileWatcher.WatchedLocation userWatcher = null;

    public ConfigurateStorage(ImpactorPlugin plugin, String implementationName, ConfigurateLoader loader, String extension, String dataDirName) {
        this.plugin = plugin;
        this.implementationName = implementationName;
        this.loader = loader;
        this.extension = extension;
        this.dataDirName = dataDirName;
    }

    public ImpactorPlugin getPlugin() {
        return this.plugin;
    }

    public String getName() {
        return this.implementationName;
    }

    public void init() throws Exception {
        if(this.plugin instanceof Configurable && this.plugin instanceof Tasking) {
            this.dataDir = ((Configurable) this.plugin).getConfigDir().resolve(this.dataDirName);
            this.createDirectoriesIfNotExists(this.dataDir);

            FileWatcher watcher = new FileWatcher(dataDir);
//            this.userWatcher = watcher.getWatcher(this.userDir);
//            this.userWatcher.addListener(path -> {
//            String s = path.getFileName().toString();
//
//            if (!s.endsWith(this.extension)) {
//                return;
//            }
//
//            String user = s.substring(0, s.length() - this.extension.length());
//            UUID uuid;
//            try {
//                uuid = UUID.fromString(user);
//            } catch (Exception e) {
//                return;
//            }
//            });
        } else {
            throw new Exception("Plugin must inherit the configurable and tasking interfaces...");
        }
    }

//    private ConfigurationNode readFile(String name) throws IOException {
//        Path file = this.userDir.resolve(name + this.extension);
//        if(this.userWatcher != null) {
//            this.userWatcher.recordChange(file.getFileName().toString());
//        }
//
//        if(!Files.exists(file)) {
//            return null;
//        }
//
//        return this.loader.loader(file).load();
//    }
//
//    private void saveFile(String name, ConfigurationNode node) throws IOException {
//        Path file = this.userDir.resolve(name + this.extension);
//        if(this.userWatcher != null) {
//            this.userWatcher.recordChange(file.getFileName().toString());
//        }
//
//        if(node == null) {
//            Files.deleteIfExists(file);
//            return;
//        }
//
//        this.loader.loader(file).save(node);
//    }

    private void createDirectoriesIfNotExists(Path path) throws IOException {
        if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
            return;
        }

        Files.createDirectories(path);
    }

    // used to report i/o exceptions which took place in a specific file
    private RuntimeException reportException(String file, Exception ex) throws RuntimeException {
        this.plugin.getPluginLogger().warn("Exception thrown whilst performing i/o: " + file);
        ex.printStackTrace();
        throw Throwables.propagate(ex);
    }

    public Path getDataDir() {
        return dataDir;
    }
}
