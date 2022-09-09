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

import net.impactdev.impactor.launcher.dependencies.relocations.Relocation;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A dependency is a runtime downloadable instance that Impactor can fetch and apply to the classpath
 * at runtime, without a user needing to download the tool. A dependency downloaded by Impactor will
 * be downloaded to the server root folder, under a folder tree of ./impactor/libs/. Once downloaded,
 * the dependency will then be injected into the classpath of the running server. Note that this method
 * will not work for plugins, as those are expected to be in the mods/plugins folders respectfully. This
 * system is meant as a means to replace shading, allowing for shaded content to exist outside the plugin
 * jar.
 * <p>
 * Each dependency is expected to exist on a compatible maven repository.
 */
public final class Dependency {

    private final String name;
    private final String group;
    private final String artifact;
    private final String version;
    private final byte[] checksum;
    private final Set<Relocation> relocations;
    private final Set<Dependency> bundled;

    public Dependency(DependencyBuilder builder) {
        this.name = builder.name;
        this.group = builder.group;
        this.artifact = builder.artifact;
        this.version = builder.version;
        this.checksum = builder.checksum != null ? Base64.getDecoder().decode(builder.checksum) : null;
        this.relocations = builder.relocations;
        this.bundled = builder.bundled;
    }

    public String name() {
        return this.name;
    }

    public String group() {
        return this.group.replace("{}", ".");
    }

    public String artifact() {
        return this.artifact;
    }

    public String version() {
        return this.version;
    }

    public boolean hashed() {
        return this.checksum != null;
    }

    public Optional<byte[]> checksum() {
        return Optional.ofNullable(this.checksum);
    }

    public Set<Relocation> relocations() {
        return this.relocations;
    }

    public String getFileName() {
        return this.artifact + "-" + this.version();
    }

    public String getMavenPath() {
        return String.format("%s/%s/%s/%s-%s.jar",
                this.group().replace(".", "/"),
                this.artifact(),
                this.version(),
                this.artifact(),
                this.version()
        );
    }

    public boolean snapshot() {
        return this.version.contains("-SNAPSHOT");
    }

    public Set<Dependency> bundled() {
        return this.bundled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependency that = (Dependency) o;
        return group.equals(that.group) && artifact.equals(that.artifact) && version.equals(that.version) && Arrays.equals(
                checksum,
                that.checksum) && relocations.equals(that.relocations);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(group, artifact, version, relocations);
        result = 31 * result + Arrays.hashCode(checksum);
        return result;
    }

    public static DependencyBuilder builder() {
        return new DependencyBuilder();
    }

    public static final class DependencyBuilder {

        private String name;
        private String group;
        private String artifact;
        private String version;
        private String checksum;
        private final Set<Relocation> relocations = new LinkedHashSet<>();
        private final Set<Dependency> bundled = new HashSet<>();

        public DependencyBuilder name(String name) {
            this.name = name;
            return this;
        }

        public DependencyBuilder group(String group) {
            this.group = group;
            return this;
        }

        public DependencyBuilder artifact(String artifact) {
            this.artifact = artifact;
            return this;
        }

        public DependencyBuilder version(String version) {
            this.version = version;
            return this;
        }

        public DependencyBuilder checksum(String checksum) {
            this.checksum = checksum;
            return this;
        }

        public DependencyBuilder relocation(Relocation relocation) {
            this.relocations.add(relocation);
            return this;
        }

        public DependencyBuilder relocations(Relocation... relocations) {
            this.relocations.addAll(Arrays.asList(relocations));
            return this;
        }

        public DependencyBuilder with(Dependency dependency) {
            this.bundled.add(dependency);
            return this;
        }

        public DependencyBuilder with(Dependency... dependencies) {
            this.bundled.addAll(Arrays.asList(dependencies));
            return this;
        }

        public DependencyBuilder from(Dependency parent) {
            this.name = parent.name;
            this.group = parent.group;
            this.artifact = parent.artifact;
            this.version = parent.version;
            this.checksum = parent.checksum != null ? Base64.getEncoder().encodeToString(parent.checksum) : null;
            this.relocations.addAll(parent.relocations);
            this.bundled.addAll(parent.bundled);
            return this;
        }

        public Dependency build() {
            return new Dependency(this);
        }
    }

}
