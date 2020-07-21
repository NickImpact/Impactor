package com.nickimpact.impactor.api.plugin.components;

import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.api.scheduler.SchedulerAdapter;

public interface Tasking extends ImpactorPlugin {

	SchedulerAdapter getScheduler();

}
