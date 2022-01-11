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

package net.impactdev.impactor.bungee.logging;

import net.impactdev.impactor.api.logging.Logger;

import java.util.List;
import java.util.function.Function;

public class BungeeLogger implements Logger {

	private final java.util.logging.Logger delegate;
	private final Function<String, String> preprocessor = in -> in.replaceAll("[&]", "\u00a7");

	public BungeeLogger(java.util.logging.Logger delegate) {
		this.delegate = delegate;
	}

	@Override
	public void noTag(String message) {
		this.info(message);
	}

	@Override
	public void noTag(List<String> message) {
		this.info(message);
	}

	@Override
	public void info(String message) {
		this.delegate.info(this.preprocessor.apply(message));
	}

	@Override
	public void info(List<String> message) {
		for(String s : message) {
			this.info(s);
		}
	}

	@Override
	public void warn(String message) {
		this.delegate.warning(this.preprocessor.apply(message));
	}

	@Override
	public void warn(List<String> message) {
		for(String s : message) {
			this.warn(s);
		}
	}

	@Override
	public void error(String message) {
		this.delegate.severe(this.preprocessor.apply(message));
	}

	@Override
	public void error(List<String> message) {
		for(String s : message) {
			this.error(s);
		}
	}

	@Override
	public void debug(String message) {
		this.delegate.info(this.preprocessor.apply("&3DEBUG &7\u00bb " + message));
	}

	@Override
	public void debug(List<String> message) {
		for(String s : message) {
			this.debug(s);
		}
	}

}
