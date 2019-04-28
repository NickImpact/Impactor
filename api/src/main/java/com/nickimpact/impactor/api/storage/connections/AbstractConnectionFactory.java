package com.nickimpact.impactor.api.storage.connections;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractConnectionFactory {

	@Getter
	private final String name;

	public abstract void init();

	public abstract void shutdown() throws Exception;
}
