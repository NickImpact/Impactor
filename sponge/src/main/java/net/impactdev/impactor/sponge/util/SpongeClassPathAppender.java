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

package net.impactdev.impactor.sponge.util;

import net.impactdev.impactor.api.dependencies.classpath.ReflectionClassPathAppender;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;

import java.lang.reflect.Field;
import java.net.URLClassLoader;

public class SpongeClassPathAppender extends ReflectionClassPathAppender {

    private static URLClassLoader extractClassLoaderFromBootstrap(ImpactorPlugin bootstrap) {
        ClassLoader classLoader = bootstrap.getClass().getClassLoader();

        // try to cast directly to URLClassLoader in case things change in the future
        if (classLoader instanceof URLClassLoader) {
            return (URLClassLoader) classLoader;
        }

        Class<? extends ClassLoader> classLoaderClass = classLoader.getClass();

        if (!classLoaderClass.getName().equals("cpw.mods.modlauncher.TransformingClassLoader")) {
            throw new IllegalStateException("ClassLoader is not instance of TransformingClassLoader: " + classLoaderClass.getName());
        }

        try {
            Field delegatedClassLoaderField = classLoaderClass.getDeclaredField("delegatedClassLoader");
            delegatedClassLoaderField.setAccessible(true);
            Object delegatedClassLoader = delegatedClassLoaderField.get(classLoader);
            return (URLClassLoader) delegatedClassLoader;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public SpongeClassPathAppender(ImpactorPlugin bootstrap) throws IllegalStateException {
        super(extractClassLoaderFromBootstrap(bootstrap));
    }
}
