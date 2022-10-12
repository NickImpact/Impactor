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

package net.impactdev.impactor.api.plugin;

import net.impactdev.impactor.api.builders.Builder;

import java.util.Optional;

public final class PluginMetadata {

	private final String id;
	private final String name;
	private final String version;
	private final String description;

	PluginMetadata(String id, String name, String version, String description) {
		this.id = id;
		this.name = name;
		this.version = version;
		this.description = description;
	}

	public String id() {
		return this.id;
	}

	public String version() {
		return this.version;
	}

	public Optional<String> name() {
		return Optional.ofNullable(this.name);
	}

	public Optional<String> description() {
		return Optional.ofNullable(this.description);
	}

	public static PluginMetadataBuilder builder() {
		return new PluginMetadataBuilder();
	}

	public static class PluginMetadataBuilder implements Builder<PluginMetadata> {
		private String id;
		private String name;
		private String version;
		private String description;

		public PluginMetadataBuilder id(String id) {
			this.id = id;
			return this;
		}

		public PluginMetadataBuilder name(String name) {
			this.name = name;
			return this;
		}

		public PluginMetadataBuilder version(String version) {
			this.version = version;
			return this;
		}

		public PluginMetadataBuilder description(String description) {
			this.description = description;
			return this;
		}

		public PluginMetadata build() {
			return new PluginMetadata(id, name, version, description);
		}

	}
}
