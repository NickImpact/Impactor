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

package net.impactdev.impactor.test;

import net.impactdev.impactor.api.logging.NoOpLogger;
import net.impactdev.impactor.test.provided.TestBootstrap;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is responsible for initializing the basic components needed to instantiate a test environment
 * for Impactor. This additionally will fire before all other BeforeAll calls, so individual class
 * files that require further setup will be able to rely on this initializer to provide
 * the API setup for each test event.
 */
public class TestInitializer implements BeforeAllCallback, AfterAllCallback, ExtensionContext.Store.CloseableResource {

    private static final Lock LOCK = new ReentrantLock();
    private static volatile boolean initialized;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        LOCK.lock();
        try {
            if(!initialized) {
                initialized = true;
                context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put("Impactor", this);

                // Initialization
                TestBootstrap bootstrap = new TestBootstrap(new NoOpLogger());
                bootstrap.init();
            }
        } finally {
            LOCK.unlock();
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        try {
            Path config = Paths.get("config");
            Path impactor = Paths.get("impactor");

            if(Files.exists(config)) {
                FileUtils.cleanDirectory(config.toFile());
                Files.delete(config);
            }

            if(Files.exists(impactor)) {
                FileUtils.cleanDirectory(impactor.toFile());
                Files.delete(impactor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Throwable {}

}
