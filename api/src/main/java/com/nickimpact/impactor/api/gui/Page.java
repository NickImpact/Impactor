package com.nickimpact.impactor.api.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

	void clean();

	void apply();

	@Getter
	@AllArgsConstructor
	class PageIcon<T> {
		private T rep;
		private int slot;
	}

	@Getter
	@RequiredArgsConstructor
	enum PageIconType {
		FIRST("&eFirst Page", (in, total) -> 1),
		PREV("&ePrevious Page", (in, total) -> in == 1 ? total : in - 1),
		CURRENT("&eCurrent Page &7(&a{{impactor_page_number}}&7)", (in, total) -> in),
		NEXT("&eNext Page", (in, total) -> in.intValue() == total.intValue() ? 1 : in + 1),
		LAST("&eLast Page", (in, total) -> total);

		private final String title;
		private final BiFunction<Integer, Integer, Integer> updater;
	}
}
