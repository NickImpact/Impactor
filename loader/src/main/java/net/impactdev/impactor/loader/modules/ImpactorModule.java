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

package net.impactdev.impactor.loader.modules;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;

/**
 * Represents an entrypoint into a module within Impactor.
 */
public abstract class ImpactorModule {

    @Inject
    protected Logger logger;

    /**
     * Describes details on the particular module.
     *
     * @return Metadata on a module
     */
    public abstract ModuleMetadata metadata();

    /**
     * Performs necessary actions required to initialize the module. Due to the nature of the loaders, any particular
     * module might throw an exception during initialization, and we should try to catch this gracefully.
     *
     * @throws Exception Any sort of exception that might occur during application launch
     */
    public abstract void initialize() throws Exception;

}
