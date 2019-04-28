package com.nickimpact.impactor.api.commands;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.commands.annotations.Aliases;

import java.util.List;

public interface Command<X, T, U, V> {

	default boolean hasRequiredAnnotations() {
		return this.getClass().isAnnotationPresent(Aliases.class);
	}

	default List<String> getAllAliases() {
		return Lists.newArrayList(this.getClass().getAnnotation(Aliases.class).value());
	}

	T getCommandSpec(String current);

	U[] getArgs();

	V getDescription();

	V getUsage();

	X[] getSubCommands();

	String buildPermission();

	void register();

}
