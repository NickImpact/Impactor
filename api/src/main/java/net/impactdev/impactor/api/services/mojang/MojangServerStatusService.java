package net.impactdev.impactor.api.services.mojang;

import net.impactdev.impactor.api.services.Service;
import lombok.Getter;

@Getter
public abstract class MojangServerStatusService implements Service {

	protected StatusChecker checker;

	public MojangServerStatusService() {
		this.checker = new StatusChecker();
		this.run();
	}

	@Override
	public String getServiceName() {
		return "Mojang Server Status";
	}

	public abstract void run();
}
