package net.impactdev.impactor.api.plugin.components;

import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.scheduler.SchedulerAdapter;

public interface Tasking extends ImpactorPlugin {

	SchedulerAdapter getScheduler();

}
