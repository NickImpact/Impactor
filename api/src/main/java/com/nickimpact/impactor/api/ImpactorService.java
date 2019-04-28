package com.nickimpact.impactor.api;

import com.nickimpact.impactor.api.registry.BuilderRegistry;
import com.nickimpact.impactor.api.registry.UniversalRegistry;

public class ImpactorService {

	private static ImpactorService instance;

	private BuilderRegistry builderRegistry;
	private UniversalRegistry universalRegistry;

	public ImpactorService() {
		instance = this;
		this.builderRegistry = new BuilderRegistry();
		this.universalRegistry = new UniversalRegistry();
	}

	public static ImpactorService getInstance() {
		return instance;
	}

	public BuilderRegistry getBuilderRegistry() {
		return this.builderRegistry;
	}

	public UniversalRegistry getUniversalRegistry() {
		return universalRegistry;
	}
}
