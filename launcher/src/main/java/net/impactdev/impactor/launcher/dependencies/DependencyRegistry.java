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

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import net.impactdev.impactor.launcher.dependencies.provided.ProvidedDependencies;
import net.impactdev.impactor.api.storage.StorageType;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DependencyRegistry {

    private static final ListMultimap<StorageType, Dependency> STORAGE_DEPENDENCIES = ImmutableListMultimap.<StorageType, Dependency>builder()
            .putAll(StorageType.YAML, ProvidedDependencies.CONFIGURATE_CORE, ProvidedDependencies.CONFIGURATE_YAML)
            .putAll(StorageType.JSON, ProvidedDependencies.CONFIGURATE_CORE, ProvidedDependencies.CONFIGURATE_GSON)
            .putAll(StorageType.HOCON, ProvidedDependencies.TYPESAFE_CONFIG, ProvidedDependencies.CONFIGURATE_CORE, ProvidedDependencies.CONFIGURATE_HOCON)
            .putAll(StorageType.MONGODB, ProvidedDependencies.MONGODB)
            .putAll(StorageType.MARIADB, ProvidedDependencies.MARIADB, ProvidedDependencies.SLF4J_API, ProvidedDependencies.SLF4J_SIMPLE, ProvidedDependencies.HIKARI)
            .putAll(StorageType.MYSQL, ProvidedDependencies.MYSQL, ProvidedDependencies.SLF4J_API, ProvidedDependencies.SLF4J_SIMPLE, ProvidedDependencies.HIKARI)
            .putAll(StorageType.H2, ProvidedDependencies.H2)
            .build();

    public Set<Dependency> resolveStorageDependencies(Collection<StorageType> storageTypes) {
        Set<Dependency> dependencies = new LinkedHashSet<>();
        for (StorageType storageType : storageTypes) {
            dependencies.addAll(STORAGE_DEPENDENCIES.get(storageType));
        }

        // don't load slf4j if it's already present
        if ((dependencies.contains(ProvidedDependencies.SLF4J_API) || dependencies.contains(ProvidedDependencies.SLF4J_SIMPLE)) && slf4jPresent()) {
            dependencies.remove(ProvidedDependencies.SLF4J_API);
            dependencies.remove(ProvidedDependencies.SLF4J_SIMPLE);
        }

        return dependencies;
    }

    private static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean slf4jPresent() {
        return classExists("org.slf4j.Logger") && classExists("org.slf4j.LoggerFactory");
    }

    public boolean shouldAutoLoad(Dependency dependency) {
        return !dontAutoLoad.contains(dependency);
    }

    private static final List<Dependency> dontAutoLoad = Lists.newArrayList(
            ProvidedDependencies.JAR_RELOCATOR,
            ProvidedDependencies.H2
    );

    static {
        try {
            Class.forName("org.objectweb.asm.Type");
            dontAutoLoad.add(ProvidedDependencies.ASM);
            dontAutoLoad.add(ProvidedDependencies.ASM_COMMONS);
        } catch (Exception ignored) {}
    }

}
