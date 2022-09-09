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

package net.impactdev.impactor.launcher.dependencies.repositories;

import net.impactdev.impactor.launcher.dependencies.Dependency;

import java.net.URL;
import java.util.Comparator;
import java.util.Optional;

/**
 * Indicates a possible repository that might contain a target dependency.
 */
public interface DependencyRepository {

    /**
     * Indicates the URL that should be queried when targeting a dependency. This URL is
     * used only when {@link Dependency#snapshot()} is <code>false</code>. If the result
     * of that call is <code>true</code>, then the repository will attempt to query its
     * link from {@link #snapshots()}, if it is set at all.
     *
     * @return The URL for the releases path for a target dependency
     */
    URL releases();

    /**
     * Indicates the URL that should be queried when targeting a dependency marked with a
     * snapshot tag. If this repository does not support snapshot querying, then this call
     * is expected to return an empty optional. In the event this repository is queried for
     * a snapshot dependency, but no snapshot URL is configured, this repository will be skipped
     * for the next in line.
     *
     * @return An optionally wrapped URL path for snapshot dependencies, if available. Otherwise,
     * {@link Optional#empty()}
     */
    Optional<SnapshotResolver> snapshots();

    /**
     * States the priority of a repository. This will determine the queue placement when
     * determining which repositories should be acted on first when attempting to discover
     * a target dependency.
     *
     * <p>When determining repository order, higher values are considered as the first
     * repositories to be queried.
     *
     * @return The priority of this repository.
     */
    int priority();

    class DependencyComparator implements Comparator<DependencyRepository> {

        @Override
        public int compare(DependencyRepository o1, DependencyRepository o2) {
            return -1 * Integer.compare(o1.priority(), o2.priority());
        }
    }

}
