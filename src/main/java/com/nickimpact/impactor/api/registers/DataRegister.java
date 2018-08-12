package com.nickimpact.impactor.api.registers;

import com.google.common.collect.Lists;
import scala.actors.threadpool.Arrays;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("ALL")
public class DataRegister<T> {

	protected List<T> data = Lists.newArrayList();

	public void register(T... data) {
		this.register(Arrays.asList(data));
	}

	public void register(Collection<T> data) {
		this.data.addAll(data);
	}

	public void remove(T data) {
		this.data.remove(data);
	}

	public void removeIf(Predicate<T> predicate) {
		this.data.removeIf(predicate);
	}

	public boolean contains(T data) {
		return this.data.contains(data);
	}
}
