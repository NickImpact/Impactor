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

package net.impactdev.impactor.loader;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.impactdev.impactor.loader.logging.PrettyPrinter;
import net.impactdev.impactor.loader.modules.ImpactorModule;
import net.impactdev.impactor.loader.modules.ModuleMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public final class ImpactorInitializer {

    static final Logger LOGGER = LogManager.getLogger("Impactor");
    private static final Marker MARKER = MarkerManager.getMarker("Launcher");
    private final Map<ModuleMetadata, Throwable> exceptions = new HashMap<>();

    public void initialize() {
        LOGGER.info(MARKER, "Launching API initialization...");

        Injector injector = Guice.createInjector(new ImpactorLoaderModule());
        ServiceLoader<ImpactorModule> loader = ServiceLoader.load(ImpactorModule.class);
        LOGGER.info(MARKER, "Loading {} modules", loader.stream().count());

        for (ImpactorModule module : loader) {
            try {
                injector.injectMembers(module);
                module.initialize();
            } catch (Throwable e) {
                ModuleMetadata metadata = module.metadata();
                LOGGER.error("Exception tracked for module: {} - {}", metadata.name(), metadata.version());
                this.exceptions.put(metadata, e);
            }
        }
    }

    @ApiStatus.Internal
    @VisibleForTesting
    public boolean anyExceptions() {
        return !exceptions.isEmpty();
    }

    public void printErrorsIfAny() {
        PrettyPrinter printer = new PrettyPrinter();
        printer.bigX().hr().title("Errors Encountered During Impactor Initialization");

        printer.add("During initialization of modules, Impactor encountered an exception or exceptions")
                .add("which prevented a successful launch. Certain aspects of Impactor may not work properly")
                .add("until these details are resolved.")
                .newline()
                .add("Exception details by module will now be listed...")
                .hr();

        AtomicInteger errors = new AtomicInteger(this.exceptions.size());
        this.exceptions.forEach((metadata, exception) -> {
            printer.add("%s - %s", metadata.name(), metadata.version().toString());
            printer.add(exception);

            if(errors.decrementAndGet() > 0) {
                printer.hr('-');
            }
        });

        printer.log(LOGGER);
    }

}
