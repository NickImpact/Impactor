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

package net.impactdev.impactor.api.dependencies;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import net.impactdev.impactor.api.storage.StorageType;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class DependencyRegistry {

	private static final ListMultimap<StorageType, Dependency> STORAGE_DEPENDENCIES = ImmutableListMultimap.<StorageType, Dependency>builder()
			.putAll(StorageType.YAML, Dependency.CONFIGURATE_CORE, Dependency.CONFIGURATE_YAML)
			.putAll(StorageType.JSON, Dependency.CONFIGURATE_CORE, Dependency.CONFIGURATE_GSON)
			.putAll(StorageType.HOCON, Dependency.HOCON_CONFIG, Dependency.CONFIGURATE_CORE, Dependency.CONFIGURATE_HOCON)
			.putAll(StorageType.MONGODB, Dependency.MONGODB_DRIVER)
			.putAll(StorageType.MARIADB, Dependency.MARIADB_DRIVER, Dependency.SLF4J_API, Dependency.SLF4J_SIMPLE, Dependency.HIKARI)
			.putAll(StorageType.MYSQL, Dependency.MYSQL_DRIVER, Dependency.SLF4J_API, Dependency.SLF4J_SIMPLE, Dependency.HIKARI)
			.putAll(StorageType.H2, Dependency.H2_DRIVER)
			.build();

	public Set<Dependency> resolveStorageDependencies(Collection<StorageType> storageTypes) {
		Set<Dependency> dependencies = new LinkedHashSet<>();
		for (StorageType storageType : storageTypes) {
			dependencies.addAll(STORAGE_DEPENDENCIES.get(storageType));
		}

		// don't load slf4j if it's already present
		if ((dependencies.contains(Dependency.SLF4J_API) || dependencies.contains(Dependency.SLF4J_SIMPLE)) && slf4jPresent()) {
			dependencies.remove(Dependency.SLF4J_API);
			dependencies.remove(Dependency.SLF4J_SIMPLE);
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
		switch (dependency) {
			// all used within 'isolated' classloaders, and are therefore not
			// relocated.
			case ASM:
			case ASM_COMMONS:
			case JAR_RELOCATOR:
			case H2_DRIVER:
				return false;
			default:
				return true;
		}
	}

}
