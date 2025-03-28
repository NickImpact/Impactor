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

package net.impactdev.impactor.api.storage;

import net.impactdev.impactor.api.storage.StorageMetadata;

import java.util.concurrent.CompletableFuture;

public interface Storage {

    /**
     * Initializes the storage, running all necessary needs in order to get the storage up and running.
     *
     * @throws Exception If anything causes the storage system to fail to initialize
     */
    void init() throws Exception;

    /**
     * Performs shutdown elements required by the storage system. This is responsible for closing connections
     * as necessary.
     *
     * @throws Exception If anything causes the storage system to fail during shutdown
     */
    void shutdown() throws Exception;

    /**
     * Returns a set of metadata describing the current state of the storage system.
     *
     * @return A future returning the metadata as it becomes available
     * @throws Exception If collecting metadata information were to fail
     */
    CompletableFuture<StorageMetadata> metadata() throws Exception;

}
