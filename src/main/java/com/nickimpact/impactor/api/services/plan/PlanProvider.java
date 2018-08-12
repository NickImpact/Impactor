package com.nickimpact.impactor.api.services.plan;

import com.djrapitops.plan.api.PlanAPI;
import com.djrapitops.plan.data.plugin.PluginData;

import java.util.Optional;

public class PlanProvider {

	private PlanAPI api = null;

	public PlanProvider() {
		try {
			api = PlanAPI.getInstance();
		} catch (Exception ignored) {}
	}

	public Optional<PlanAPI> fetchAPI() {
		return Optional.ofNullable(api);
	}

	public void register(PluginData data) throws IllegalStateException {
		fetchAPI().orElseThrow(() -> new IllegalStateException("PlanAPI is currently unavailable...")).addPluginDataSource(data);
	}
}
