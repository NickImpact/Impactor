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

package net.impactdev.impactor.api.gui;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Page<T, U, W extends UI, V extends Icon> {

	T getViewer();

	W getView();

	Page<T, U, W, V> applier(Function<U, V> applier);

	void define(List<U> contents);

	void open();

	void close();

	void apply();

	class PageIcon<T> {
		private final T rep;
		private final int slot;

		public PageIcon(T rep, int slot) {
			this.rep = rep;
			this.slot = slot;
		}

		public T getRep() {
			return this.rep;
		}

		public int getSlot() {
			return this.slot;
		}
	}

	enum PageIconType {
		FIRST("&eFirst Page", (in, total) -> 1),
		PREV("&ePrevious Page", (in, total) -> in == 1 ? total : in - 1),
		CURRENT("&eCurrent Page &7(&a{{impactor_page_number}}&7)", (in, total) -> in),
		NEXT("&eNext Page", (in, total) -> in.intValue() == total.intValue() ? 1 : in + 1),
		LAST("&eLast Page", (in, total) -> total);

		private final String title;
		private final BiFunction<Integer, Integer, Integer> updater;

		PageIconType(String title, BiFunction<Integer, Integer, Integer> updater) {
			this.title = title;
			this.updater = updater;
		}

		public String getTitle() {
			return this.title;
		}

		public BiFunction<Integer, Integer, Integer> getUpdater() {
			return this.updater;
		}
	}
}
