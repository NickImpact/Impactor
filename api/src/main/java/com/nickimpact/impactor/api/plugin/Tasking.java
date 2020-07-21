package com.nickimpact.impactor.api.plugin;

import java.util.concurrent.ScheduledExecutorService;

public interface Tasking extends ImpactorPlugin {

	ScheduledExecutorService getAsyncExecutor();

}
