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

package net.impactdev.impactor.api.dependencies.repositories;

import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Optional;

public enum ProvidedRepositories implements DependencyRepository {

    /**
     * The primary Impactor maven repository. This is where most Impactor repositories are created.
     */
    IMPACT_DEV(
            from("https://maven.impactdev.net/repository/development/"),
            resolver("https://maven.impactdev.net/service/rest/v1/search/assets/",
                    ((parent, dependency) -> from(parent, String.format(
                            "download?repository=development&group=%s&name=%s&sort=version&maven.extension=jar&maven.classifier",
                            dependency.group(),
                            dependency.artifact()
                    )))
            ),
            100
    ),

    /**
     * The primary ImpactDev repository, with an additional Maven Repo mirror.
     *
     * <p>This is used to reduce the load on repo.maven.org</p>
     */
    // Please ask me (NickImpact) before using this mirror in your own project.
    IMPACTDEV_MIRROR(from("https://maven.impactdev.net/repository/maven-central/"), 50),

    /**
     * The primary ImpactDev repository, with an additional Maven Repo mirror.
     *
     * <p>This is used to reduce the load on repo.maven.org</p>
     */
    // Please ask me (NickImpact) before using this mirror in your own project.
    IMPACTDEV_SONATYPE_MIRROR(from("https://maven.impactdev.net/repository/Sonatype/"), 50),
    /**
     * Maven Central.
     */
    MAVEN_CENTRAL(from("https://repo1.maven.org/maven2/"), 50),
    ;

    private final URL releases;
    private final SnapshotResolver snapshots;

    private final int priority;

    ProvidedRepositories(final URL releases, final int priority) {
        this(releases, null, priority);
    }

    ProvidedRepositories(final URL releases, final @Nullable SnapshotResolver snapshots, final int priority) {
        this.releases = releases;
        this.snapshots = snapshots;
        this.priority = priority;
    }

    private static URL from(String url) {
        try {
            return new URL(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static URL from(URL parent, String child) {
        try {
            return new URL(parent, child);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static SnapshotResolver resolver(String url, SnapshotResolver.Resolver resolver) {
        return SnapshotResolver.builder()
                .path(from(url))
                .resolver(resolver)
                .build();
    }

    @Override
    public URL releases() {
        return this.releases;
    }

    @Override
    public Optional<SnapshotResolver> snapshots() {
        return Optional.ofNullable(this.snapshots);
    }

    @Override
    public int priority() {
        return this.priority;
    }
}
