package com.nickimpact.impactor.api.plugins;

import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.configuration.ConfigBase;
import com.nickimpact.impactor.api.logger.Logger;
import com.nickimpact.impactor.api.services.plan.PlanData;
import org.spongepowered.api.scheduler.Task;

import java.util.List;
import java.util.Optional;

public interface IPlugin extends Configurable {

	PluginInfo getPluginInfo();

	Logger getLogger();

	Optional<PlanData> getPlanData();

	List<ConfigBase> getConfigs();

	List<SpongeCommand> getCommands();

	List<Object> getListeners();

	void onDisconnect();

	void reload();

	void onReload();
}
