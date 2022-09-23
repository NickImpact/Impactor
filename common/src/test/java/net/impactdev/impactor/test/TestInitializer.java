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

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import net.impactdev.impactor.api.APIRegister;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.ImpactorService;
import net.impactdev.impactor.modules.ImpactorModule;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is responsible for initializing the basic components needed to instantiate a test environment
 * for Impactor. This additionally will fire before all other BeforeAll calls, so individual class
 * files that require further setup will be able to rely on this initializer to provide
 * the API setup for each test event.
 */
public class TestInitializer implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

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
                Impactor impactor = new ImpactorService();
                APIRegister.register(impactor);

                ClassGraph graph = new ClassGraph().acceptPackages("net.impactdev.impactor").enableClassInfo();
                try (ScanResult scan = graph.scan()) {
                    ClassInfoList list = scan.getClassesImplementing(ImpactorModule.class);
                        list.stream()
                            .map(info -> info.loadClass(ImpactorModule.class))
                            .map(type -> {
                                try {
                                    return type.newInstance();
                                }
                                catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .forEach(module -> {
                                module.factories(impactor.factories());
                                module.builders(impactor.builders());
                                module.services(impactor.services());
                            });
                }
            }
        } finally {
            LOCK.unlock();
        }
    }

    @Override
    public void close() throws Throwable {}

}
