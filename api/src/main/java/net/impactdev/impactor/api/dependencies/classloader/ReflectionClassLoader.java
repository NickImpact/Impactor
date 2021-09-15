/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2021 NickImpact
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

package net.impactdev.impactor.api.dependencies.classloader;

import net.impactdev.impactor.api.plugin.ImpactorPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.function.Function;

public class ReflectionClassLoader implements PluginClassLoader {

	private static URLClassLoader extractClassLoaderFromBootstrap(ImpactorPlugin bootstrap) {
		return castToUrlClassLoader(bootstrap.getClass().getClassLoader());
	}

	public static URLClassLoader castToUrlClassLoader(Object classLoader) {
		if (classLoader instanceof URLClassLoader) {
			return (URLClassLoader) classLoader;
		} else {
			throw new IllegalStateException("ClassLoader is not instance of URLClassLoader: " + classLoader.getClass().getName());
		}
	}


	private static final Method ADD_URL_METHOD;

	static {
		try {
			ADD_URL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			ADD_URL_METHOD.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private final URLClassLoader classLoader;

	public ReflectionClassLoader(ImpactorPlugin bootstrap) throws IllegalStateException {
		this(bootstrap, ReflectionClassLoader::extractClassLoaderFromBootstrap);
	}

	public ReflectionClassLoader(ImpactorPlugin bootstrap, Function<ImpactorPlugin, ? extends URLClassLoader> classLoader) {
		this.classLoader = classLoader.apply(bootstrap);
	}

	@Override
	public void addJarToClasspath(Path file) {
		try {
			ADD_URL_METHOD.invoke(this.classLoader, file.toUri().toURL());
		} catch (IllegalAccessException | InvocationTargetException | MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}