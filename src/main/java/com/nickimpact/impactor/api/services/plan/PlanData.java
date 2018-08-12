package com.nickimpact.impactor.api.services.plan;

import com.djrapitops.plan.data.plugin.PluginData;
import lombok.Getter;

@Getter
public class PlanData {

	private PluginData data;

	public PlanData(PluginData data) {
		this.data = data;
	}
}
