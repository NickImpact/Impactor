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

package net.impactdev.impactor.api.services;

import java.util.Optional;

public interface ServiceManager {

	/**
	 * Attempts to locate and return a Service matching the typing of the class marker. If a service has not yet
	 * been registered under this typing, this call will return with an empty value.
	 *
	 * @param type The type of service being requested
	 * @param <T> The type shared between the design and implementation of a service
	 * @return An optionally wrapped value possibly containing the service, if it has been initialized
	 */
	<T extends Service> Optional<T> get(Class<T> type);

	/**
	 * Registers a service that can be later queried and used for quick and essential operations.
	 *
	 * @param type The type of the service being registered
	 * @param service The implementation of that service
	 * @param <T> The common type between the design and implementation of a service
	 * @throws IllegalArgumentException If the service typing has already been registered
	 */
	<T extends Service> void register(Class<T> type, T service) throws IllegalArgumentException;

}
